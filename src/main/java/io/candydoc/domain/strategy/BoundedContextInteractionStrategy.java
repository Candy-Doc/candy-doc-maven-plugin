package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.model.DDDConcept;
import java.util.List;

public class BoundedContextInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(DDDConcept concept) {
    return List.of();
  }
}
