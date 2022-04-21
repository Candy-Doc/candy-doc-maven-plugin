package io.candydoc.domain.exceptions;

import io.candydoc.domain.model.DDDConcept;

public class ConceptNotAnnotatedByDDDConcept extends DomainException {
  public ConceptNotAnnotatedByDDDConcept(DDDConcept wrongConcept) {
    super("Concept: " + wrongConcept.getName() + " is not annotated by DDD concept");
  }
  ;
}
