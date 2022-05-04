package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.aggregate.AggregatesInteractionStrategy;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.bounded_context.BoundedContextInteractionStrategy;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.core_concept.CoreConceptInteractionStrategy;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_command.DomainCommandInteractionStrategy;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.domain_event.DomainEventInteractionStrategy;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDConcept;
import io.candydoc.ddd.shared_kernel.SharedKernel;
import io.candydoc.ddd.shared_kernel.SharedKernelInteractionStrategy;
import io.candydoc.ddd.value_object.ValueObject;
import io.candydoc.ddd.value_object.ValueObjectInteractionStrategy;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InteractionChecker {

  private final AggregatesInteractionStrategy aggregatesInteractionStrategy;
  private final BoundedContextInteractionStrategy boundedContextInteractionStrategy;
  private final CoreConceptInteractionStrategy coreConceptInteractionStrategy;
  private final DomainCommandInteractionStrategy domainCommandInteractionStrategy;
  private final DomainEventInteractionStrategy domainEventInteractionStrategy;
  private final SharedKernelInteractionStrategy sharedKernelInteractionStrategy;
  private final ValueObjectInteractionStrategy valueObjectInteractionStrategy;
  private final DDDConceptFinder conceptFinder;

  public InteractionChecker(DDDConceptFinder conceptFinder) {
    this.conceptFinder = conceptFinder;
    aggregatesInteractionStrategy = new AggregatesInteractionStrategy(conceptFinder);
    boundedContextInteractionStrategy = new BoundedContextInteractionStrategy(conceptFinder);
    coreConceptInteractionStrategy = new CoreConceptInteractionStrategy(conceptFinder);
    domainCommandInteractionStrategy = new DomainCommandInteractionStrategy(conceptFinder);
    domainEventInteractionStrategy = new DomainEventInteractionStrategy(conceptFinder);
    sharedKernelInteractionStrategy = new SharedKernelInteractionStrategy(conceptFinder);
    valueObjectInteractionStrategy = new ValueObjectInteractionStrategy(conceptFinder);
  }

  @SneakyThrows
  public List<Event> check(CheckConceptInteractions command) {
    return conceptFinder
        .findConcept(CanonicalName.of(command.getConceptName()))
        .apply(
            new DDDConcept.Visitor<>() {
              @Override
              public List<Event> aggregate(Aggregate aggregate) {
                return aggregatesInteractionStrategy.checkInteractions(aggregate);
              }

              @Override
              public List<Event> boundedContext(BoundedContext boundedContext) {
                return boundedContextInteractionStrategy.checkInteractions(boundedContext);
              }

              @Override
              public List<Event> coreConcept(CoreConcept coreConcept) {
                return coreConceptInteractionStrategy.checkInteractions(coreConcept);
              }

              @Override
              public List<Event> domainCommand(DomainCommand domainCommand) {
                return domainCommandInteractionStrategy.checkInteractions(domainCommand);
              }

              @Override
              public List<Event> domainEvent(DomainEvent domainEvent) {
                return domainEventInteractionStrategy.checkInteractions(domainEvent);
              }

              @Override
              public List<Event> sharedKernel(SharedKernel sharedKernel) {
                return sharedKernelInteractionStrategy.checkInteractions(sharedKernel);
              }

              @Override
              public List<Event> valueObject(ValueObject valueObject) {
                return valueObjectInteractionStrategy.checkInteractions(valueObject);
              }
            });
  }
}
