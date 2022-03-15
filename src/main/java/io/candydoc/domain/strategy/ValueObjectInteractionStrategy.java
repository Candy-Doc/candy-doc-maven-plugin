package io.candydoc.domain.strategy;

import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ValueObjectInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Class<?> concept) {
    if (!extractDDDInteractions(concept).isEmpty()) {
      return List.of(
          ConceptRuleViolated.builder()
              .className(concept.getName())
              .reason("Value Object should only contain primitive types")
              .build());
    }
    return List.of();
  }

  private Set<Class<?>> extractInteractingClasses(Class<?> currentConcept) {
    Set<Class<?>> classesInCurrentConcept =
        Arrays.stream(currentConcept.getDeclaredFields())
            .map(Field::getType)
            .collect(Collectors.toSet());
    classesInCurrentConcept.addAll(
        Arrays.stream(currentConcept.getDeclaredMethods())
            .map(
                method -> {
                  List<Class<?>> parameterClasses =
                      new ArrayList<>(List.of(method.getParameterTypes()));
                  parameterClasses.add(method.getReturnType());
                  return parameterClasses;
                })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet()));
    return classesInCurrentConcept;
  }

  public Set<InteractionBetweenConceptFound> extractDDDInteractions(Class<?> currentConcept) {
    Set<Class<?>> classesInCurrentConcept = extractInteractingClasses(currentConcept);

    return classesInCurrentConcept.stream()
        .filter(
            classInCurrentConcept ->
                DDD_ANNOTATION_CLASSES.stream()
                    .anyMatch(classInCurrentConcept::isAnnotationPresent))
        .map(
            interactingConcept ->
                InteractionBetweenConceptFound.builder()
                    .from(currentConcept.getName())
                    .with(interactingConcept.getName())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }
}