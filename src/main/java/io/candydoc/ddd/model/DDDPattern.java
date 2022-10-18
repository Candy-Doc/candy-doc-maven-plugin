package io.candydoc.ddd.model;

public interface DDDPattern {

    enum Tactical implements DDDPattern {
        BOUNDED_CONTEXT,
        SHARED_KERNEL,
        AGGREGATE,
        DOMAIN_EVENT,
        DOMAIN_COMMAND,
        VALUE_OBJECT;
    }

    enum Strategic implements DDDPattern {
        BOUNDED_CONTEXT,
        SHARED_KERNEL;
    }

}
