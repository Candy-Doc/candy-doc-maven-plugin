package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;

public class BoundedContextInteractionStrategy implements InteractionStrategy<BoundedContext> {

  public List<Event> checkInteractions(BoundedContext concept) {
    return List.of();
  }
}
