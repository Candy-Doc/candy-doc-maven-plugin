package io.candydoc.ddd;

import io.candydoc.ddd.aggregate.ExtractAggregates;
import io.candydoc.ddd.core_concept.ExtractCoreConcepts;
import io.candydoc.ddd.domain_command.ExtractDomainCommands;
import io.candydoc.ddd.domain_event.ExtractDomainEvents;
import io.candydoc.ddd.extract_ddd_concepts.ExtractDDDConcepts;
import io.candydoc.ddd.interaction.CheckConceptInteractions;
import io.candydoc.ddd.value_object.ExtractValueObjects;

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
