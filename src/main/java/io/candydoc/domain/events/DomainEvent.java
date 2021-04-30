package io.candydoc.domain.events;

public interface DomainEvent {

    void accept(Visitor visitor);

    interface Visitor {
        void apply(BoundedContextFound event);
        void apply(CoreConceptFound event);
        void apply(InteractionBetweenConceptFound event);
        void apply(ValueObjectFound event);
        void apply(DomainEventFound event);

        void apply(NameConflictBetweenCoreConcept event);
        void apply(WrongUsageOfValueObjectFound event);
    };
}
