package io.candydoc.domain.strategy;

import io.candydoc.domain.annotations.Aggregate;
import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDInteraction;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class CoreConceptInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(DDDConcept concept) {
    List<DomainEvent> domainEvents = new LinkedList<>();
    domainEvents.addAll(extractWrongInteractions(concept));
    domainEvents.addAll(extractDDDInteractions(concept));
    return domainEvents;
  }

  private List<DomainEvent> extractWrongInteractions(DDDConcept currentConcept) {
    Set<DDDInteraction> interactionsInCurrentConcept =
        currentConcept.getFields().stream()
            .map(
                dddField ->
                    DDDInteraction.builder()
                        .name(dddField.getName())
                        .annotation(dddField.getType())
                        .build())
            .collect(Collectors.toSet());
    return interactionsInCurrentConcept.stream()
        .filter(
            interactionInCurrentConcept ->
                interactionInCurrentConcept.getAnnotation().equals(Aggregate.class))
        .map(
            wrongClass ->
                ConceptRuleViolated.builder()
                    .className(currentConcept.getCanonicalName())
                    .reason("CoreConcept interact with Aggregates " + wrongClass.getName() + ".")
                    .build())
        .collect(Collectors.toList());
  }

  private Set<DDDInteraction> extractInteractingClasses(DDDConcept currentConcept) {
    Set<DDDInteraction> interactionsInCurrentConcept =
        currentConcept.getFields().stream()
            .map(
                dddField ->
                    DDDInteraction.builder()
                        .name(dddField.getName())
                        .canonicalName(dddField.getType().getCanonicalName())
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
                                            .canonicalName(parameterType.getCanonicalName())
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
            interactingConcept -> {
              String interactingConceptName =
                  interactingConcept.getCanonicalName() != null
                      ? interactingConcept.getCanonicalName()
                      : interactingConcept.getName();
              return InteractionBetweenConceptFound.builder()
                  .from(currentConcept.getCanonicalName())
                  .with(interactingConceptName)
                  .build();
            })
        .collect(Collectors.toUnmodifiableSet());
  }
}
