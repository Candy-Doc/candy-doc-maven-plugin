package io.candydoc.domain.exceptions;

public class ClassNotAnnotatedByDDDConcept extends DomainException{
    public ClassNotAnnotatedByDDDConcept(Class<?> wrongClasse)
    {super("Classe: " + wrongClasse.getName() + " is not annotated by DDD concept");};
}
