package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.model.ExtractionException;

public class NoBoundedContextFound extends ExtractionException {
  public NoBoundedContextFound(String packageToScan) {
    super("No bounded context has been found in the package : '" + packageToScan + "'.");
  }
}
