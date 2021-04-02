package io.candydoc.domain.exceptions;

import lombok.Getter;

import java.util.List;

public class WrongUsageOfBoundedContext extends DomainException {

    @Getter
    private final List<Class<?>> wrongClasses;

    public WrongUsageOfBoundedContext(List<Class<?>> wrongClasses) {
        super("Bounded context annotation must only be on a package-info : " + wrongClasses);
        this.wrongClasses = wrongClasses;
    }
}
