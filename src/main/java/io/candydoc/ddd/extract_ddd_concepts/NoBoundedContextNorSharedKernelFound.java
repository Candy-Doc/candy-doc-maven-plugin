package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.model.ExtractionException;
import java.util.List;

public class NoBoundedContextNorSharedKernelFound extends ExtractionException {
  public NoBoundedContextNorSharedKernelFound(List<String> packageToScans) {
    super(
        String.format(
            "No bounded context nor shared kernel has been found in this packages : '%s'.",
            packageToScans));
  }
}
