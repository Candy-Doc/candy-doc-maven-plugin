package io.candydoc.ddd;

import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.core_concept.NameConflictBetweenCoreConcepts;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.shared_kernel.SharedKernelFound;
import io.candydoc.ddd.value_object.ValueObjectFound;

public interface Event {

  void accept(Visitor visitor);

  interface Visitor {
    void apply(AggregateFound event);

    void apply(BoundedContextFound event);

    void apply(CoreConceptFound event);

    void apply(DomainCommandFound event);

    void apply(DomainEventFound event);

    void apply(SharedKernelFound event);

    void apply(ValueObjectFound event);

    void apply(ConceptRuleViolated event);

    void apply(InteractionBetweenConceptFound event);

    void apply(NameConflictBetweenCoreConcepts event);
  }
}
