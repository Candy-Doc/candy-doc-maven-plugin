package io.candydoc.domain.extractor;

import io.candydoc.domain.command.*;
import io.candydoc.domain.events.*;
import io.candydoc.domain.repository.ProcessorUtils;
import io.candydoc.domain.strategy.InteractionChecker;

import javax.tools.Diagnostic;
import java.util.LinkedList;
import java.util.List;

public class DDDConceptExtractor
    implements Command.Visitor, DomainEvent.Visitor, Extractor<Command> {
  private final List<DomainEvent> eventsList = new LinkedList<>();
  private final ValueObjectExtractor valueObjectExtractor = new ValueObjectExtractor();
  private final AggregatesExtractor aggregatesExtractor = new AggregatesExtractor();
  private final BoundedContextExtractor boundedContextExtractor = new BoundedContextExtractor();
  private final CoreConceptExtractor coreConceptExtractor = new CoreConceptExtractor();
  private final DomainEventExtractor domainEventExtractor = new DomainEventExtractor();
  private final DomainCommandExtractor domainCommandExtractor = new DomainCommandExtractor();
  private final InteractionChecker interactionChecker = new InteractionChecker();

  @Override
  public List<DomainEvent> extract(Command command) {
    command.accept(this);
    return eventsList;
  }

  private void trackAndApply(List<DomainEvent> occurredEvents) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "trackAndApply of occurredEvents");
    eventsList.addAll(occurredEvents);
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "trackAndApply of " + occurredEvents.size() + " events done");
    occurredEvents.forEach(event -> event.accept(this));
  }

  public void handle(ExtractValueObjects command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract value objects from " + command.getPackageToScan());
    trackAndApply(valueObjectExtractor.extract(command));
  }

  public void handle(ExtractDDDConcepts command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract ddd concepts from " + command.getPackagesToScan());
    trackAndApply(boundedContextExtractor.extract(command));
  }

  public void handle(ExtractCoreConcepts command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract core concepts from " + command.getPackageToScan());
    trackAndApply(coreConceptExtractor.extract(command));
  }

  public void handle(ExtractDomainEvents command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract domain events from " + command.getPackageToScan());
    trackAndApply(domainEventExtractor.extract(command));
  }

  public void handle(ExtractDomainCommands command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract domain commands from " + command.getPackageToScan());
    trackAndApply(domainCommandExtractor.extract(command));
  }

  public void handle(CheckConceptInteractions command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Check concept interactions from " + command.getClassName());
    trackAndApply(interactionChecker.check(command));
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "trackAndApply of interactionChecker done");
  }

  public void handle(ExtractAggregates command) {
    ProcessorUtils.getInstance().getMessager().printMessage(Diagnostic.Kind.NOTE, "Extract aggregates from " + command.getPackageToScan());
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

  public void apply(InteractionBetweenConceptFound event) {
  }

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

  public void apply(NameConflictBetweenCoreConcepts event) {
  }

  public void apply(ConceptRuleViolated event) {
  }
}
