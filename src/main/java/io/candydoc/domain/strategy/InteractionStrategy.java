package io.candydoc.domain.strategy;

import io.candydoc.domain.events.DomainEvent;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public interface InteractionStrategy {
    List<DomainEvent> checkInteractions(Class<?> concept);

    static final Set<Class<? extends Annotation>> DDD_ANNOTATION_CLASSES = Set.of(io.candydoc.domain.annotations.BoundedContext.class,
    io.candydoc.domain.annotations.CoreConcept.class,
    io.candydoc.domain.annotations.ValueObject.class,
    io.candydoc.domain.annotations.DomainEvent.class,
    io.candydoc.domain.annotations.DomainCommand.class);
}
