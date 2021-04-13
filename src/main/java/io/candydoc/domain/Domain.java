package io.candydoc.domain;

import io.candydoc.domain.exceptions.*;
import io.candydoc.domain.model.BoundedContext;
import io.candydoc.domain.model.CoreConcept;
import io.candydoc.domain.model.ValueObject;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Value
@RequiredArgsConstructor
@Slf4j
public class Domain {

    SaveDocumentationPort saveDocumentationPort;
    Logger logger = Logger.getLogger("logger");

    public void checkParameters(GenerateDocumentation command) throws DocumentationGenerationFailed {
        if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
            throw new DocumentationGenerationFailed("Missing parameters for 'packageToScan'. Check your pom configuration.");
        }
    }

    public void generateDocumentation(GenerateDocumentation command) throws IOException, DomainException {
        checkParameters(command);
        List<BoundedContext> boundedContexts = extractBoundedContexts(command.getPackagesToScan());
        saveDocumentationPort.save(boundedContexts);
        logger.info("Documentation generation has succeeded.");
    }

    public List<BoundedContext> extractBoundedContexts(List<String> packagesToScan) throws DomainException {
        logger.info("Bounded contexts extraction has started...");
        return packagesToScan.stream()
                .map(this::extractBoundedContexts)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<BoundedContext> extractBoundedContexts(String packageToScan) throws DomainException {
        if (packageToScan.isBlank()) {
            throw new DocumentationGenerationFailed("Empty parameters for 'packagesToScan'. Check your pom configuration");
        }
        Reflections reflections = new Reflections(packageToScan);
        Set<Class<?>> rawBoundedContexts = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);
        if (rawBoundedContexts.isEmpty()) {
            throw new NoBoundedContextFound(packageToScan);
        }
        Map<Boolean, List<Class<?>>> filteredBoundedContexts = rawBoundedContexts.stream()
                .collect(Collectors.partitioningBy(bc -> "package-info".equals(bc.getSimpleName())));
        if (filteredBoundedContexts.get(false) != null && !filteredBoundedContexts.get(false).isEmpty()) {
            throw new WrongUsageOfBoundedContext(filteredBoundedContexts.get(false));
        }
        return filteredBoundedContexts.get(true).stream()
                .map(boundedContext -> BoundedContext.builder()
                        .name(boundedContext.getPackageName())
                        .description(boundedContext.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).description())
                        .coreConcepts(extractCoreConcepts(boundedContext.getPackageName()))
                        .valueObjects(extractValueObjects(boundedContext.getPackageName()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<CoreConcept> extractCoreConcepts(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> coreConceptClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        List<CoreConcept> coreConcepts = coreConceptClasses.stream()
                .map(coreConcept -> CoreConcept.builder()
                        .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                        .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                        .className(coreConcept.getName())
                        .interactsWith(extractDDDInteractions(coreConcept))
                        .build())
                .collect(Collectors.toList());
        if (coreConcepts.stream().map(CoreConcept::getName).collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1L)
                .map(Map.Entry::getKey).count() > 0) {
            throw new DocumentationGenerationFailed("Multiple core concepts share the same name in a bounded context");
        }
        return coreConcepts;
    }

    public List<ValueObject> extractValueObjects(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> valueObjectClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
        List<ValueObject> valueObjects = valueObjectClasses.stream()
                .map(valueObject -> ValueObject.builder()
                        .description(valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class).description())
                        .className(valueObject.getName())
                        .build())
                .collect(Collectors.toList());

        List<Class<?>> wrongValueObjects = valueObjectClasses.stream()
                .filter(clazz -> !extractDDDInteractions(clazz).isEmpty())
                .collect(Collectors.toUnmodifiableList());
        if (!wrongValueObjects.isEmpty()) {
            throw new WrongUsageOfValueObject(wrongValueObjects);
        }
        return valueObjects;
    }

    private Set<Class<?>> getSetOfDDDAnnotations() {
        return Set.of(io.candydoc.domain.annotations.BoundedContext.class,
                io.candydoc.domain.annotations.CoreConcept.class,
                io.candydoc.domain.annotations.ValueObject.class);
    }

    public Set<Class<?>> getClassesAnnotatedWithDDDAnnotations(Class<?> classToScan) {
        Set<Class<?>> DDDAnnotations = getSetOfDDDAnnotations();
        return Arrays.stream(classToScan.getAnnotations())
                .map(Annotation::annotationType)
                .filter(DDDAnnotations::contains)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Class<?>> extractInteractingClasses(Class<?> currentConcept) {
        Set<Class<?>> classesInCurrentConcept = Arrays.stream(currentConcept.getDeclaredFields())
                .map(Field::getType)
                .collect(Collectors.toSet());
        classesInCurrentConcept.addAll(Arrays.stream(currentConcept.getDeclaredMethods())
                .map(method -> {
                    List<Class<?>> parameterClasses = new ArrayList<>(List.of(method.getParameterTypes()));
                    parameterClasses.add(method.getReturnType());
                    return parameterClasses;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet()));
        return classesInCurrentConcept;
    }

    public Set<String> extractDDDInteractions(Class<?> currentConcept) {
        Set<Class<?>> classesInCurrentConcept = extractInteractingClasses(currentConcept);
        return classesInCurrentConcept.stream()
                .filter(classInCurrentConcept -> classInCurrentConcept
                        .getAnnotation(io.candydoc.domain.annotations.CoreConcept.class) != null || classInCurrentConcept
                        .getAnnotation(io.candydoc.domain.annotations.ValueObject.class) != null)
                .map(Class::getName)
                .collect(Collectors.toUnmodifiableSet());
    }
}