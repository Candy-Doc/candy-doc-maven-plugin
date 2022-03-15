package io.candydoc.domain.exceptions;

public class NoBoundedContextFound extends DomainException {
  public NoBoundedContextFound(String packageToScan) {
    super("No bounded context has been found in the package : '" + packageToScan + "'.");
  }
}
