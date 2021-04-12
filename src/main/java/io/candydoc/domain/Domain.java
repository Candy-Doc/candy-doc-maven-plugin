package io.candydoc.domain;

import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.DomainException;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.exceptions.WrongUsageOfBoundedContext;
import io.candydoc.domain.model.BoundedContext;
import io.candydoc.domain.model.CoreConcept;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.io.IOException;
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
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);
        if (set.isEmpty()) {
            throw new NoBoundedContextFound(packageToScan);
        }
        Map<Boolean, List<Class<?>>> filteredBoundedContexts = set.stream()
                .collect(Collectors.partitioningBy(bc -> "package-info".equals(bc.getSimpleName())));
        if (filteredBoundedContexts.get(false) != null && !filteredBoundedContexts.get(false).isEmpty()) {
            throw new WrongUsageOfBoundedContext(filteredBoundedContexts.get(false));
        }
        return filteredBoundedContexts.get(true).stream()
                .map(boundedContext -> BoundedContext.builder()
                        .name(boundedContext.getPackageName())
                        .description(boundedContext.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).description())
                        .coreConcepts(extractCoreConcepts(boundedContext.getPackageName()))
                        .build())
                .collect(Collectors.toList());
    }

    public void checkParameters(GenerateDocumentation command) throws DocumentationGenerationFailed {
        if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
            throw new DocumentationGenerationFailed("Missing parameters for 'packageToScan'. Check your pom configuration.");
        }
    }

    public List<CoreConcept> extractCoreConcepts(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> coreConceptClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        List<CoreConcept> concepts = coreConceptClasses.stream()
                .map(coreConcept -> CoreConcept.builder()
                        .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                        .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                        .className(coreConcept.getName())
                        .interactsWith(extractInteractions(coreConcept))
                        .build())
                .collect(Collectors.toList());
        if (concepts.stream().map(CoreConcept::getName).collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1L)
                .map(Map.Entry::getKey).count() > 0) {
            throw new DocumentationGenerationFailed("Multiple core concepts share the same name in a bounded context");
        }
        return concepts;
    }

    public Set<String> extractInteractions(Class<?> currentCoreConcept) {
        Set<Class<?>> classesInCurrentCoreConcept = Arrays.stream(currentCoreConcept.getDeclaredFields())
                .map(Field::getType)
                .collect(Collectors.toSet());
        classesInCurrentCoreConcept.addAll(Arrays.stream(currentCoreConcept.getDeclaredMethods())
                .map(method -> {
                    List<Class<?>> parameterClasses = new ArrayList<>(List.of(method.getParameterTypes()));
                    parameterClasses.add(method.getReturnType());
                    return parameterClasses;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet()));
        return classesInCurrentCoreConcept.stream()
                .filter(classInCurrentCoreConcept -> classInCurrentCoreConcept
                        .getAnnotation(io.candydoc.domain.annotations.CoreConcept.class) != null)
                .map(Class::getName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
