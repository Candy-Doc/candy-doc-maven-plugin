package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ConceptRuleViolated;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainEventInteractionStrategy implements InteractionStrategy {

    public List<DomainEvent> checkInteractions(Class<?> concept) {
        Set<Class<?>> classesInCurrentConcept = Arrays.stream(concept.getDeclaredFields())
                .map(Field::getType)
                .collect(Collectors.toSet());
        return classesInCurrentConcept.stream()
                .filter(classInCurrentConcept -> DDD_ANNOTATION_CLASSES.stream()
                        .anyMatch(classInCurrentConcept::isAnnotationPresent))
                .map(match -> ConceptRuleViolated.builder()
                        .conceptFullName(concept.getName())
                        .reason("Wrong interaction with concept " + match.getName() + ".")
                        .build())
                .collect(Collectors.toList());
    }
}
