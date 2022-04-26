package io.candydoc.ddd.annotations;

import java.lang.annotation.Annotation;
import java.util.Set;

public class DDDKeywords {

    public static final Set<Class<? extends Annotation>> KEYWORDS = Set.of(
        BoundedContext.class,
        CoreConcept.class,
        ValueObject.class,
        DomainEvent.class,
        DomainCommand.class,
        Aggregate.class
    );

    private DDDKeywords() {
    }

}
