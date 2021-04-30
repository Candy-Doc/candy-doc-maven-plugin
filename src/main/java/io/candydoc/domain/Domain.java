package io.candydoc.domain;

import io.candydoc.domain.events.*;
import io.candydoc.domain.events.NameConflictBetweenCoreConcept;
import io.candydoc.domain.events.WrongUsageOfValueObjectFound;
import io.candydoc.domain.exceptions.*;
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
        List<DomainEvent> domainEvents = extractBoundedContexts(command.getPackagesToScan());
        saveDocumentationPort.save(domainEvents);
        logger.info("Documentation generation has succeeded.");
    }

    public List<DomainEvent> extractBoundedContexts(List<String> packagesToScan) {
        logger.info("Bounded contexts extraction has started...");
        return packagesToScan.stream()
                .map(packageToScan -> extractBoundedContexts(packageToScan)).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    public List<DomainEvent> extractBoundedContexts(String packageToScan) {
        List<DomainEvent> occurredEvents = new LinkedList<>();
        List<DomainEvent> wrongDomainEvents = List.of();
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
         occurredEvents.addAll(filteredBoundedContexts.get(true).stream()
                .map(boundedContext -> {
                    BoundedContextFound newBoundedContextEvent = BoundedContextFound.builder()
                        .name(boundedContext.getPackageName())
                        .description(boundedContext.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).description())
                        .build();
                        List<DomainEvent> events = new LinkedList<>(Collections.singleton(newBoundedContextEvent));
                        events.addAll(extractCoreConcepts(newBoundedContextEvent.getName()));
                        events.addAll(extractValueObjects(newBoundedContextEvent.getName()));
                        events.addAll(extractDomainEvent(newBoundedContextEvent.getName()));
                        return events;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        occurredEvents.addAll(wrongDomainEvents);
        return occurredEvents;
    }

    public List<DomainEvent> extractValueObjects(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> valueObjectClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
        List<DomainEvent> valueObjects = valueObjectClasses.stream()
                .map(valueObject -> ValueObjectFound.builder()
                        .description(valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class).description())
                        .className(valueObject.getName())
                        .boundedContext(boundedContextToScan)
                        .build())
                .collect(Collectors.toList());
        List<Class<?>> wrongValueObjects = valueObjectClasses.stream()
                .filter(clazz -> !extractDDDInteractions(clazz).isEmpty())
                .collect(Collectors.toUnmodifiableList());
        if (!wrongValueObjects.isEmpty()) {
            wrongValueObjects.stream().forEach(wrongValueObject -> valueObjects.add(WrongUsageOfValueObjectFound.builder()
                    .valueObject(wrongValueObject.getName())
                    .usageError("Value Object should only contain primitive type")
                    .build()));
        }
        return valueObjects;
    }

    public List<DomainEvent> verifyCoreConcept(Set<Class<?>> coreConceptClasses) {
        List<CoreConceptFound> coreConcepts = coreConceptClasses.stream()
                .map(coreConcept -> {List<CoreConceptFound> eventList = new LinkedList<>(Collections.singleton(CoreConceptFound.builder()
                        .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                        .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                        .className(coreConcept.getName())
                        .boundedContext("error handling")
                        .build()));
                    return eventList;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return coreConcepts.stream()
                .map(definedCoreConcept -> definedCoreConcept.getName()).collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1L)
                .map(DuplicatedCoreConcept -> NameConflictBetweenCoreConcept.builder()
                        .conflictingCoreConcepts(coreConcepts.stream()
                                .filter(coreConcept -> coreConcept.getName().equals(DuplicatedCoreConcept.getKey()))
                                .map(coreConceptFound -> coreConceptFound.getClassName())
                                .collect(Collectors.toList()))
                        .UsageError("Share same name with another core concept")
                        .build())
                .collect(Collectors.toList());
    }

    public List<DomainEvent> extractCoreConcepts(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> coreConceptClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        List<DomainEvent> wrongCoreConcept = verifyCoreConcept(coreConceptClasses);
        List<DomainEvent> coreConcepts = coreConceptClasses.stream()
                .map(coreConcept -> {List<DomainEvent> eventList = new LinkedList<>(Collections.singleton(CoreConceptFound.builder()
                        .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                        .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                        .className(coreConcept.getName())
                        .boundedContext(boundedContextToScan)
                        .build()));
                    eventList.addAll(extractDDDInteractions(coreConcept));
                    return eventList;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        coreConcepts.addAll(wrongCoreConcept);
        return coreConcepts;
    }

    List<DomainEvent> extractDomainEvent(String boundedContextToScan) {
        Reflections reflections = new Reflections(boundedContextToScan);
        Set<Class<?>> domainEventClass = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.DomainEvent.class);
        List<DomainEvent> domainEvents = domainEventClass.stream()
                .map(domainEvent -> {List<DomainEvent> eventList = new LinkedList<>(Collections.singleton(DomainEventFound.builder()
                        .description(domainEvent.getAnnotation(io.candydoc.domain.annotations.DomainEvent.class).description())
                        .className(domainEvent.getName())
                        .boundedContext(boundedContextToScan)
                        .build()));
                    eventList.addAll(extractDDDInteractions(domainEvent));
                    return eventList;})
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return domainEvents;
    }

    private Set<Class<? extends Annotation>> getSetOfDDDAnnotations() {
        return Set.of(io.candydoc.domain.annotations.BoundedContext.class,
                io.candydoc.domain.annotations.CoreConcept.class,
                io.candydoc.domain.annotations.ValueObject.class,
                io.candydoc.domain.annotations.DomainEvent.class);
    }

    public Set<Class<?>> getClassesAnnotatedWithDDDAnnotations(Class<?> classToScan) {
        Set<Class<? extends Annotation>> DDDAnnotations = getSetOfDDDAnnotations();
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

    public Set<InteractionBetweenConceptFound> extractDDDInteractions(Class<?> currentConcept) {
        Set<Class<?>> classesInCurrentConcept = extractInteractingClasses(currentConcept);
        Set<Class<? extends Annotation>> conceptAnnotations = getSetOfDDDAnnotations();

        return classesInCurrentConcept.stream()
                .filter(classInCurrentConcept -> conceptAnnotations.stream().anyMatch(classInCurrentConcept::isAnnotationPresent))
                .map(interactingConcept -> InteractionBetweenConceptFound.builder()
                        .from(currentConcept.getName())
                        .with(interactingConcept.getName())
                        .build())
                .collect(Collectors.toUnmodifiableSet());
    }
}