package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.model.ExtractionException;
import java.util.List;

public class NoBoundedContextNorSharedKernelFound extends ExtractionException {
  public NoBoundedContextNorSharedKernelFound(List<String> packageToScans) {
    super(
        "No bounded context or shared kernel has been found in this packages : '"
            + packageToScans
            + "'.");
  }
}
