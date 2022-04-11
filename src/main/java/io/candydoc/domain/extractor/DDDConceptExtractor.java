package io.candydoc.domain.extractor;

import io.candydoc.domain.command.*;
import io.candydoc.domain.events.*;
import io.candydoc.domain.strategy.InteractionChecker;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDDConceptExtractor
    implements Command.Visitor, DomainEvent.Visitor, Extractor<Command> {

  private final List<DomainEvent> eventsList = new LinkedList<>();
  private final ValueObjectExtractor valueObjectExtractor;
  private final AggregatesExtractor aggregatesExtractor;
  private final BoundedContextExtractor boundedContextExtractor;
  private final CoreConceptExtractor coreConceptExtractor;
  private final DomainEventExtractor domainEventExtractor;
  private final DomainCommandExtractor domainCommandExtractor;
  private final InteractionChecker interactionChecker = new InteractionChecker();

  public DDDConceptExtractor(DDDConceptFinder DDDConceptFinder) {
    this.valueObjectExtractor = new ValueObjectExtractor(DDDConceptFinder);
    this.aggregatesExtractor = new AggregatesExtractor(DDDConceptFinder);
    this.boundedContextExtractor = new BoundedContextExtractor(DDDConceptFinder);
    this.coreConceptExtractor = new CoreConceptExtractor(DDDConceptFinder);
    this.domainEventExtractor = new DomainEventExtractor(DDDConceptFinder);
    this.domainCommandExtractor = new DomainCommandExtractor(DDDConceptFinder);
  }

  @Override
  public List<DomainEvent> extract(Command command) {
    command.accept(this);
    return eventsList;
  }

  private void trackAndApply(List<DomainEvent> occurredEvents) {
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
    log.info("Check concept interactions from {}", command.getClassName());
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
    this.handle(CheckConceptInteractions.builder().className(event.getClassName()).build());
  }

  public void apply(InteractionBetweenConceptFound event) {}

  public void apply(ValueObjectFound event) {
    this.handle(CheckConceptInteractions.builder().className(event.getClassName()).build());
  }

  public void apply(DomainEventFound event) {
    this.handle(CheckConceptInteractions.builder().className(event.getClassName()).build());
  }

  public void apply(DomainCommandFound event) {
    this.handle(CheckConceptInteractions.builder().className(event.getClassName()).build());
  }

  public void apply(AggregateFound event) {
    this.handle(CheckConceptInteractions.builder().className(event.getClassName()).build());
  }

  public void apply(NameConflictBetweenCoreConcepts event) {}

  public void apply(ConceptRuleViolated event) {}
}
