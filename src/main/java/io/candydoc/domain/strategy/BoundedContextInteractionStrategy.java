package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;

import javax.lang.model.element.Element;
import java.util.List;

public class BoundedContextInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Element concept) {
    return List.of();
  }
}
