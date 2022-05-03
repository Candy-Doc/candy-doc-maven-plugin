package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.model.ExtractionException;
import java.util.List;

public class NoBoundedContextOrSharedKernelFound extends ExtractionException {
  public NoBoundedContextOrSharedKernelFound(List<String> packageToScans) {
    super(
        "No bounded context or shared kernel has been found in this packages : '"
            + packageToScans
            + "'.");
  }
}
