package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.annotations.*;
import io.candydoc.domain.model.DDDConcept;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public interface InteractionStrategy {
  List<Event> checkInteractions(DDDConcept concept);

  Set<Class<? extends Annotation>> DDD_ANNOTATION_CLASSES =
      Set.of(
          BoundedContext.class,
          CoreConcept.class,
          ValueObject.class,
          io.candydoc.ddd.annotations.DomainEvent.class,
          DomainCommand.class,
          Aggregate.class);
}
