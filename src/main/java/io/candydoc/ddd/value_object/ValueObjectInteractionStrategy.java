package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDInteraction;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ValueObjectInteractionStrategy implements InteractionStrategy {

  public List<Event> checkInteractions(DDDConcept concept) {
    if (!extractDDDInteractions(concept).isEmpty()) {
      return List.of(
          ConceptRuleViolated.builder()
              .className(concept.getCanonicalName())
              .reason("Value Object should only contain primitive types")
              .build());
    }
    return List.of();
  }

  private Set<DDDInteraction> extractInteractingClasses(DDDConcept currentConcept) {
    Set<DDDInteraction> interactionsInCurrentConcept =
        currentConcept.getFields().stream()
            .map(
                dddField ->
                    DDDInteraction.builder()
                        .name(dddField.getName())
                        .annotation(
                            Arrays.stream(dddField.getType().getAnnotations())
                                .filter(
                                    annotation ->
                                        DDD_ANNOTATION_CLASSES.contains(
                                            annotation.annotationType()))
                                .findFirst()
                                .get()
                                .annotationType())
                        .build())
            .collect(Collectors.toSet());
    interactionsInCurrentConcept.addAll(
        currentConcept.getMethods().stream()
            .map(
                method -> {
                  List<DDDInteraction> parameterClasses = new ArrayList<>();
                  parameterClasses.addAll(
                      method.getParameterTypes().stream()
                          .map(
                              parameterType -> {
                                List<DDDInteraction> interactions = new ArrayList<>();
                                for (Annotation annotation : parameterType.getAnnotations()) {
                                  if (DDD_ANNOTATION_CLASSES.contains(annotation.annotationType()))
                                    interactions.add(
                                        DDDInteraction.builder()
                                            .name(parameterType.getName())
                                            .annotation(annotation.annotationType())
                                            .build());
                                }
                                return interactions;
                              })
                          .flatMap(Collection::stream)
                          .collect(Collectors.toList()));
                  parameterClasses.add(
                      DDDInteraction.builder()
                          .name(method.getName())
                          .annotation(method.getReturnType())
                          .build());
                  return parameterClasses;
                })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet()));
    return interactionsInCurrentConcept;
  }

  public Set<InteractionBetweenConceptFound> extractDDDInteractions(DDDConcept currentConcept) {
    Set<DDDInteraction> interactionsInCurrentConcept = extractInteractingClasses(currentConcept);
    return interactionsInCurrentConcept.stream()
        .filter(
            interactionInCurrentConcept ->
                DDD_ANNOTATION_CLASSES.contains(interactionInCurrentConcept.getAnnotation()))
        .map(
            interactingConcept ->
                InteractionBetweenConceptFound.builder()
                    .from(currentConcept.getCanonicalName())
                    .with(interactingConcept.getName())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }
}
