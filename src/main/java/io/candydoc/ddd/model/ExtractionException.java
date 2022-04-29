package io.candydoc.ddd.model;

public class ExtractionException extends RuntimeException {
  public ExtractionException(String errorMessage) {
    super(errorMessage);
  }
}
