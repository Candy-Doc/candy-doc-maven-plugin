package io.candydoc.ddd.interaction;

import io.candydoc.ddd.model.ExtractionException;
import io.candydoc.domain.model.DDDConcept;

// Todo: Rename it
public class ConceptNotAnnotatedByDDDConcept extends ExtractionException {
  public ConceptNotAnnotatedByDDDConcept(DDDConcept wrongConcept) {
    super("Concept: " + wrongConcept.getName() + " is not annotated by DDD concept");
  }
}
