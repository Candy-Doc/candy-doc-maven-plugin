package io.candydoc.domain.extractor;

import io.candydoc.domain.command.*;
import io.candydoc.domain.events.*;
import io.candydoc.domain.strategy.InteractionChecker;

import java.util.LinkedList;
import java.util.List;

public class DDDConceptExtractor implements Command.Visitor, DomainEvent.Visitor, Extractor<Command> {
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

    private void trackAndApply(List<DomainEvent> occuredEvents) {
        eventsList.addAll(occuredEvents);
        occuredEvents.forEach(event -> event.accept(this));
    }

    public void handle(ExtractValueObject command) {
        trackAndApply(valueObjectExtractor.extract(command));
    }

    public void handle(ExtractDDDConcept command) {
        trackAndApply(boundedContextExtractor.extract(command));
    }

    public void handle(ExtractCoreConcept command) {
        trackAndApply(coreConceptExtractor.extract(command));
    }

    public void handle(ExtractDomainEvent command) {
        trackAndApply(domainEventExtractor.extract(command));
    }

    public void handle(ExtractDomainCommand command) {
        trackAndApply(domainCommandExtractor.extract(command));
    }

    public void handle(CheckConceptInteractions command) {
        trackAndApply(interactionChecker.check(command));
    }

    public void handle(ExtractAggregates command) {
        trackAndApply(aggregatesExtractor.extract(command));
    }

    public void apply(BoundedContextFound event) {
        this.handle(ExtractCoreConcept.builder()
                .packageToScan(event.getPackageName())
                .build());
        this.handle(ExtractValueObject.builder()
                .packageToScan(event.getPackageName())
                .build());
        this.handle(ExtractDomainEvent.builder()
                .packageToScan(event.getPackageName())
                .build());
        this.handle(ExtractDomainCommand.builder()
                .packageToScan(event.getPackageName())
                .build());
        this.handle(ExtractAggregates.builder()
                .packageToScan(event.getPackageName())
                .build());
    }

    public void apply(CoreConceptFound event) {
        this.handle(CheckConceptInteractions.builder()
                .className(event.getFullName())
                .build());
    }

    public void apply(InteractionBetweenConceptFound event) {
    }

    public void apply(ValueObjectFound event) {
        this.handle(CheckConceptInteractions.builder()
                .className(event.getFullName())
                .build());
    }

    public void apply(DomainEventFound event) {
        this.handle(CheckConceptInteractions.builder()
                .className(event.getFullName())
                .build());
    }

    public void apply(DomainCommandFound event) {
        this.handle(CheckConceptInteractions.builder()
                .className(event.getFullName())
                .build());
    }

    public void apply(AggregateFound event) {
        this.handle(CheckConceptInteractions.builder()
                .className(event.getFullName())
                .build());
    }

    public void apply(NameConflictBetweenCoreConcept event) {
    }

    public void apply(ConceptRuleViolated event) {
    }
}