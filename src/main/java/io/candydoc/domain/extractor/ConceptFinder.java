package io.candydoc.domain.extractor;

import io.candydoc.domain.model.DDDConcept;

import java.util.Set;

public interface ConceptFinder {
  Set<DDDConcept> findAggregate(String packageToScan);
  Set<DDDConcept> findBoundedContext(String packageToScan);
  Set<DDDConcept> findCoreConcept(String packageToScan);
  Set<DDDConcept> findDomainCommand(String packageToScan);
  Set<DDDConcept> findDomainEvent(String packageToScan);
  Set<DDDConcept> findValueObject(String packageToScan);
}
