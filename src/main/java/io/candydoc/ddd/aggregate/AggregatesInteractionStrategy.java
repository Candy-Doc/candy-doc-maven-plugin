package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AggregatesInteractionStrategy implements InteractionStrategy<Aggregate> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(Aggregate concept) {
    return conceptFinder.findInteractionsWith(concept.getCanonicalName()).stream()
        .map(interaction -> conceptFinder.findConcept(interaction.canonicalName()))
        .map(
            anotherConcept ->
                InteractionBetweenConceptFound.builder()
                    .from(concept.getCanonicalName().value())
                    .with(anotherConcept.getCanonicalName().value())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
