package io.candydoc.ddd.extract_domain_vocabulary;

public class ExtractionException extends RuntimeException {
    public ExtractionException(String reason) {
        super(reason);
    }
}
