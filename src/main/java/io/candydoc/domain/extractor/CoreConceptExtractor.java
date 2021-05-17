package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcept;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.NameConflictBetweenCoreConcept;
import org.reflections8.Reflections;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoreConceptExtractor implements Extractor<ExtractCoreConcept> {

    private List<DomainEvent> verifyCoreConcept(Set<Class<?>> coreConceptClasses) {
        List<CoreConceptFound> coreConcepts = coreConceptClasses.stream()
                .map(coreConcept -> CoreConceptFound.builder()
                        .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                        .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                        .className(coreConcept.getSimpleName())
                        .fullName(coreConcept.getName())
                        .packageName(coreConcept.getPackageName())
                        .boundedContext("error handling")
                        .build())
                .collect(Collectors.toList());
        return coreConcepts.stream()
                .map(CoreConceptFound::getName).collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1L)
                .map(foundDuplicate -> NameConflictBetweenCoreConcept.builder()
                        .conflictingCoreConcepts(coreConcepts.stream()
                                .filter(coreConcept -> coreConcept.getName().equals(foundDuplicate.getKey()))
                                .map(CoreConceptFound::getClassName)
                                .collect(Collectors.toList()))
                        .UsageError("Share same name with another core concept")
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<DomainEvent> extract(ExtractCoreConcept command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> coreConceptClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        List<DomainEvent> wrongCoreConcept = verifyCoreConcept(coreConceptClasses);
        List<DomainEvent> coreConcepts = coreConceptClasses.stream()
                .map(coreConcept -> (List<DomainEvent>) new LinkedList<DomainEvent>(Collections.singleton(
                        CoreConceptFound.builder()
                                .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                                .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                                .className(coreConcept.getSimpleName())
                                .fullName(coreConcept.getName())
                                .packageName(coreConcept.getPackageName())
                                .boundedContext(command.getPackageToScan())
                                .build())))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        coreConcepts.addAll(wrongCoreConcept);
        return coreConcepts;
    }
}
