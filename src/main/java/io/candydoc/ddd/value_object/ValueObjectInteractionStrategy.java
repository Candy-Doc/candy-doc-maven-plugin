package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValueObjectInteractionStrategy implements InteractionStrategy<ValueObject> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(ValueObject concept) {
    if (conceptFinder.findInteractionsWith(concept.getCanonicalName()).isEmpty()) {
      return List.of();
    }
    return List.of(
        ConceptRuleViolated.builder()
            .conceptName(concept.getCanonicalName().value())
            .reason("Value Object should only contain primitive types")
            .build());
  }
}
