package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDInteraction;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregatesInteractionStrategy implements InteractionStrategy {
  public List<Event> checkInteractions(DDDConcept concept) {
    return new LinkedList<>(extractDDDInteractions(concept));
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
                dddMethod ->
                    DDDInteraction.builder()
                        .name(dddMethod.getName())
                        .annotation(dddMethod.getReturnType())
                        .build())
            .collect(Collectors.toSet()));
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
