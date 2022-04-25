package io.candydoc.ddd.interaction;

import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.model.ExtractionException;

// Todo: Rename it
public class ConceptNotAnnotatedByDDDConcept extends ExtractionException {
  public ConceptNotAnnotatedByDDDConcept(DDDConcept wrongConcept) {
    super("Concept: " + wrongConcept.getCanonicalName() + " is not annotated by DDD concept");
  }
}
