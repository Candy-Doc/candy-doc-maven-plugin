package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.model.Interaction;
import io.candydoc.ddd.value_object.ValueObject;
import java.lang.annotation.Annotation;
import java.util.Set;

public interface DDDConceptFinder {
  Set<Class<? extends Annotation>> DDD_ANNOTATION_CLASSES =
      Set.of(
          io.candydoc.ddd.annotations.BoundedContext.class,
          io.candydoc.ddd.annotations.CoreConcept.class,
          io.candydoc.ddd.annotations.ValueObject.class,
          io.candydoc.ddd.annotations.DomainEvent.class,
          io.candydoc.ddd.annotations.DomainCommand.class,
          io.candydoc.ddd.annotations.Aggregate.class);

  Set<Aggregate> findAggregates(String packageToScan);

  Set<BoundedContext> findBoundedContexts(String packageToScan);

  Set<CoreConcept> findCoreConcepts(String packageToScan);

  Set<DomainCommand> findDomainCommands(String packageToScan);

  Set<DomainEvent> findDomainEvents(String packageToScan);

  Set<ValueObject> findValueObjects(String packageToScan);

  Set<Interaction> findInteractionsWith(CanonicalName canonicalName);

  DDDConcept findConcept(CanonicalName canonicalName);
}
