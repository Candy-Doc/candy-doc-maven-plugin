package io.candydoc.domain.exceptions;

public class DocumentationGenerationFailed extends DomainException{
    public DocumentationGenerationFailed(String errorMessage) {
        super(errorMessage);
    }
}

