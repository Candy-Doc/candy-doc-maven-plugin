package io.candydoc.domain.extractor;

import io.candydoc.domain.model.DDDConcept;
import java.util.Set;

public interface DDDConceptFinder {
  Set<DDDConcept> findAggregates(String packageToScan);

  Set<DDDConcept> findBoundedContexts(String packageToScan);

  Set<DDDConcept> findCoreConcepts(String packageToScan);

  Set<DDDConcept> findDomainCommands(String packageToScan);

  Set<DDDConcept> findDomainEvents(String packageToScan);

  Set<DDDConcept> findValueObjects(String packageToScan);
}
