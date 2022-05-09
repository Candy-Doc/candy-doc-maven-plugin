package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.model.Interaction;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.shared_kernel.SharedKernel;
import io.candydoc.ddd.value_object.ValueObject;
import java.util.Set;

public interface DDDConceptFinder {

  Set<DDDConcept> findDDDConcepts();

  Set<DDDConcept> findDDDConcepts(PackageName packageName);

  Set<Aggregate> findAggregates(String packageToScan);

  Set<BoundedContext> findBoundedContexts(String packageToScan);

  Set<CoreConcept> findCoreConcepts(String packageToScan);

  Set<DomainCommand> findDomainCommands(String packageToScan);

  Set<DomainEvent> findDomainEvents(String packageToScan);

  Set<SharedKernel> findSharedKernels(String packageToScan);

  Set<ValueObject> findValueObjects(String packageToScan);

  Set<Interaction> findInteractionsWith(CanonicalName conceptName);

  DDDConcept findConcept(CanonicalName conceptName);
}
