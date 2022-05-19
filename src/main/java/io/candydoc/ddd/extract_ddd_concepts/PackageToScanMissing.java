package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.model.ExtractionException;

public class PackageToScanMissing extends ExtractionException {
  public PackageToScanMissing(String errorMessage) {
    super(errorMessage);
  }
}
