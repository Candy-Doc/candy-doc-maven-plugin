package io.candydoc.domain.exceptions;

public class ClassNotAnnotatedByDDDConcept extends DomainException {
    public ClassNotAnnotatedByDDDConcept(Class<?> wrongClass) {
        super("Class: " + wrongClass.getName() + " is not annotated by DDD concept");
    }

    ;
}
