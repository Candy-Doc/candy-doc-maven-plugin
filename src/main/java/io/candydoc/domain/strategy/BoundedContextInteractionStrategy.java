package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;

import java.util.List;

public class BoundedContextInteractionStrategy implements InteractionStrategy{

    public List<DomainEvent> checkInteractions(Class<?> concept) {
        return List.of();
    }

}
