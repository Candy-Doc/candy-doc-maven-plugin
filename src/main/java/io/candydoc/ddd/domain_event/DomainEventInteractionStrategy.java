package io.candydoc.ddd.domain_event;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DomainEventInteractionStrategy implements InteractionStrategy<DomainEvent> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(DomainEvent concept) {
    return conceptFinder.findInteractionsWith(concept.getCanonicalName()).stream()
        .map(interaction -> conceptFinder.findConcept(interaction.canonicalName()))
        .map(
            anotherConcept ->
                ConceptRuleViolated.builder()
                    .conceptName(concept.getCanonicalName().value())
                    .reason(
                        "Wrong interaction with class "
                            + anotherConcept.getCanonicalName().value()
                            + ".")
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}