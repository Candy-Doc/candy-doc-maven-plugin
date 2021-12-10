package io.candydoc.domain.command;

public interface Command {
    void accept(Visitor visitor);

    interface Visitor {
        void handle(ExtractValueObjects command);
        void handle(ExtractDDDConcepts command);
        void handle(ExtractCoreConcepts command);
        void handle(ExtractDomainEvents command);
        void handle(ExtractDomainCommands command);
        void handle(CheckConceptInteractions command);
        void handle(ExtractAggregates command);
    }
}
