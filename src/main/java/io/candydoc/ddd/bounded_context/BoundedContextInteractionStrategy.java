package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.InteractionStrategy;
import io.candydoc.domain.model.DDDConcept;
import java.util.List;

public class BoundedContextInteractionStrategy implements InteractionStrategy {

  public List<Event> checkInteractions(DDDConcept concept) {
    return List.of();
  }
}
