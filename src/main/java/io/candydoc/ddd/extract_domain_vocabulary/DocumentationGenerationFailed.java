package io.candydoc.ddd.extract_domain_vocabulary;

public class DocumentationGenerationFailed extends RuntimeException {
    public DocumentationGenerationFailed(Throwable throwable) {
        super(throwable);
    }
}
