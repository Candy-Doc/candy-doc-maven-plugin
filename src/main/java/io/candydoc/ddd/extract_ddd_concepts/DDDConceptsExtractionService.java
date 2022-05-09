package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.aggregate.AggregatesExtractor;
import io.candydoc.ddd.aggregate.ExtractAggregates;
import io.candydoc.ddd.bounded_context.BoundedContextExtractor;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptExtractor;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.core_concept.ExtractCoreConcepts;
import io.candydoc.ddd.core_concept.NameConflictBetweenCoreConcepts;
import io.candydoc.ddd.domain_command.DomainCommandExtractor;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_command.ExtractDomainCommands;
import io.candydoc.ddd.domain_event.DomainEventExtractor;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.domain_event.ExtractDomainEvents;
import io.candydoc.ddd.interaction.CheckConceptInteractions;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.interaction.InteractionChecker;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.ddd.value_object.ExtractValueObjects;
import io.candydoc.ddd.value_object.ValueObjectExtractor;
import io.candydoc.ddd.value_object.ValueObjectFound;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDDConceptsExtractionService
    implements Command.Visitor, Event.Visitor, Extractor<Command> {

  private final List<Event> eventsList = new LinkedList<>();

  private final ValueObjectExtractor valueObjectExtractor;
  private final AggregatesExtractor aggregatesExtractor;
  private final BoundedContextExtractor boundedContextExtractor;
  private final CoreConceptExtractor coreConceptExtractor;
  private final DomainEventExtractor domainEventExtractor;
  private final DomainCommandExtractor domainCommandExtractor;
  private final InteractionChecker interactionChecker;

  public DDDConceptsExtractionService(DDDConceptFinder conceptFinder) {
    this.valueObjectExtractor = new ValueObjectExtractor(conceptFinder);
    this.aggregatesExtractor = new AggregatesExtractor(conceptFinder);
    this.boundedContextExtractor = new BoundedContextExtractor(conceptFinder);
    this.coreConceptExtractor = new CoreConceptExtractor(conceptFinder);
    this.domainEventExtractor = new DomainEventExtractor(conceptFinder);
    this.domainCommandExtractor = new DomainCommandExtractor(conceptFinder);
    this.interactionChecker = new InteractionChecker(conceptFinder);
  }

  @Override
  public List<Event> extract(Command command) {
    command.accept(this);
    return eventsList;
  }

  private void trackAndApply(List<Event> occurredEvents) {
    eventsList.addAll(occurredEvents);
    occurredEvents.forEach(event -> event.accept(this));
  }

  public void handle(ExtractValueObjects command) {
    log.info("Extract value objects from {}", command.getPackageToScan());
    trackAndApply(valueObjectExtractor.extract(command));
  }

  public void handle(ExtractDDDConcepts command) {
    log.info("Extract ddd concepts from {}", command.getPackagesToScan());
    trackAndApply(boundedContextExtractor.extract(command));
  }

  public void handle(ExtractCoreConcepts command) {
    log.info("Extract core concepts from {}", command.getPackageToScan());
    trackAndApply(coreConceptExtractor.extract(command));
  }

  public void handle(ExtractDomainEvents command) {
    log.info("Extract domain events from {}", command.getPackageToScan());
    trackAndApply(domainEventExtractor.extract(command));
  }

  public void handle(ExtractDomainCommands command) {
    log.info("Extract domain commands from {}", command.getPackageToScan());
    trackAndApply(domainCommandExtractor.extract(command));
  }

  public void handle(CheckConceptInteractions command) {
    log.info("Check concept interactions from {}", command.getConceptName());
    trackAndApply(interactionChecker.check(command));
  }

  public void handle(ExtractAggregates command) {
    log.info("Extract aggregates from {}", command.getPackageToScan());
    trackAndApply(aggregatesExtractor.extract(command));
  }

  public void apply(BoundedContextFound event) {
    this.handle(ExtractCoreConcepts.builder().packageToScan(event.getPackageName()).build());
    this.handle(ExtractValueObjects.builder().packageToScan(event.getPackageName()).build());
    this.handle(ExtractDomainEvents.builder().packageToScan(event.getPackageName()).build());
    this.handle(ExtractDomainCommands.builder().packageToScan(event.getPackageName()).build());
    this.handle(ExtractAggregates.builder().packageToScan(event.getPackageName()).build());
  }

  public void apply(CoreConceptFound event) {
    this.handle(CheckConceptInteractions.builder().conceptName(event.getCanonicalName()).build());
  }

  public void apply(InteractionBetweenConceptFound event) {}

  public void apply(ValueObjectFound event) {
    this.handle(CheckConceptInteractions.builder().conceptName(event.getCanonicalName()).build());
  }

  public void apply(DomainEventFound event) {
    this.handle(CheckConceptInteractions.builder().conceptName(event.getCanonicalName()).build());
  }

  public void apply(DomainCommandFound event) {
    this.handle(CheckConceptInteractions.builder().conceptName(event.getCanonicalName()).build());
  }

  public void apply(AggregateFound event) {
    this.handle(CheckConceptInteractions.builder().conceptName(event.getCanonicalName()).build());
  }

  public void apply(NameConflictBetweenCoreConcepts event) {}

  public void apply(ConceptRuleViolated event) {}
}
