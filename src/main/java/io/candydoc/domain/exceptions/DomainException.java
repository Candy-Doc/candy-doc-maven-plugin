package io.candydoc.domain.exceptions;

public class DomainException extends RuntimeException{
    public DomainException(String errorMessage) {
        super(errorMessage);
    }
}
