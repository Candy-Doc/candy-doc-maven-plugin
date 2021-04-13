package io.candydoc.domain.exceptions;

import lombok.Getter;

import java.util.List;

public class WrongUsageOfValueObject extends DomainException{
    @Getter
    private final List<Class<?>> wrongClasses;

    public WrongUsageOfValueObject(List<Class<?>> wrongClasses) {
        super("Value object should use primitive types only: "+ wrongClasses);
        this.wrongClasses = wrongClasses;
    }
}
