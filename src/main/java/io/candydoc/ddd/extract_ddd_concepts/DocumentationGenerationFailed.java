package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.model.ExtractionException;

public class DocumentationGenerationFailed extends ExtractionException {
  public DocumentationGenerationFailed(String errorMessage) {
    super(errorMessage);
  }
}
