package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcepts;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.NameConflictBetweenCoreConcepts;
import org.reflections8.Reflections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreConceptExtractor implements Extractor<ExtractCoreConcepts> {

    @Override
    public List<DomainEvent> extract(ExtractCoreConcepts command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> coreConceptClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.CoreConcept.class);
        List<CoreConceptFound> coreConcepts = findCoreConcepts(command, coreConceptClasses);
        List<DomainEvent> conflicts = checkConflictBetweenCoreConcepts(coreConcepts);
        return Stream.of(coreConcepts, conflicts)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    private List<DomainEvent> checkConflictBetweenCoreConcepts(List<CoreConceptFound> foundCoreConcepts) {
        return foundCoreConcepts.stream()
            .collect(Collectors.groupingBy(CoreConceptFound::getName))
            .values()
            .stream()
            .filter(coreConceptFounds -> coreConceptFounds.size() > 1)
            .map(duplicateConcepts -> NameConflictBetweenCoreConcepts.builder()
                .coreConceptClassNames(duplicateConcepts.stream()
                    .map(CoreConceptFound::getClassName)
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toUnmodifiableList());
    }

    private List<CoreConceptFound> findCoreConcepts(ExtractCoreConcepts command, Set<Class<?>> coreConceptClasses) {
        return coreConceptClasses.stream()
            .map(coreConcept -> CoreConceptFound.builder()
                .name(getSimpleName(coreConcept))
                .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                .className(coreConcept.getName())
                .packageName(coreConcept.getPackageName())
                .boundedContext(command.getPackageToScan())
                .build())
            .collect(Collectors.toUnmodifiableList());
    }

    private String getSimpleName(Class<?> aggregate) {
        String annotatedName = aggregate.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name();
        return annotatedName.isBlank() ? aggregate.getSimpleName() : annotatedName;
    }
}
