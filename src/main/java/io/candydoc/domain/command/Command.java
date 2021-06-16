package io.candydoc.domain.command;

public interface Command {
    void accept(Visitor visitor);

    interface Visitor {
        void handle(ExtractValueObject command);
        void handle(ExtractDDDConcept command);
        void handle(ExtractCoreConcept command);
        void handle(ExtractDomainEvent command);
        void handle(ExtractDomainCommand command);
        void handle(CheckConceptInteractions command);
        void handle(ExtractAggregates command);
    }
}
