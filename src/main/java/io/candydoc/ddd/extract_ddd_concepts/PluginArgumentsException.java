package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.model.ExtractionException;

public class PluginArgumentsException extends ExtractionException {
  public PluginArgumentsException(String errorMessage) {
    super(errorMessage);
  }
}
