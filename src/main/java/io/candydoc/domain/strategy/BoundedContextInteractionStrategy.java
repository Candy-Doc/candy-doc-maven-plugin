package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;
import java.util.List;
import javax.lang.model.element.Element;

public class BoundedContextInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Element concept) {
    return List.of();
  }
}
