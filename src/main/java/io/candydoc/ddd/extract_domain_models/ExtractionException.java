package io.candydoc.ddd.extract_domain_models;

public class ExtractionException extends RuntimeException {
    public ExtractionException(String reason) {
        super(reason);
    }
}
