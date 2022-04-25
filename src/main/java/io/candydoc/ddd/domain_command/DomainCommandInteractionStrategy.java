package io.candydoc.ddd.domain_command;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDInteraction;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainCommandInteractionStrategy implements InteractionStrategy {
  public List<Event> checkInteractions(DDDConcept concept) {
    Set<DDDInteraction> interactionsInCurrentConcept =
        concept.getFields().stream()
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
    return interactionsInCurrentConcept.stream()
        .filter(
            interactionInCurrentConcept ->
                DDD_ANNOTATION_CLASSES.contains(interactionInCurrentConcept.getAnnotation()))
        .map(
            match ->
                ConceptRuleViolated.builder()
                    .className(concept.getName())
                    .reason("Wrong interaction with class " + match.getName() + ".")
                    .build())
        .collect(Collectors.toList());
  }
}
