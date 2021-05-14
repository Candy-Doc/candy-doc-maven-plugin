package io.candydoc.domain.extractor;

import io.candydoc.domain.strategy.InteractionChecker;
import io.candydoc.domain.command.*;
import io.candydoc.domain.events.*;

import java.util.LinkedList;
import java.util.List;

public class DDDConceptExtractor implements Command.Visitor, DomainEvent.Visitor, Extractor<Command> {
    private List<DomainEvent> eventsList = new LinkedList<>();
    private final ValueObjectExtractor valueObjectExtractor = new ValueObjectExtractor();
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

    public void handle(CheckConceptInteraction command) {
        trackAndApply(interactionChecker.check(command));
    }

    public void apply(BoundedContextFound event) {
        this.handle(ExtractCoreConcept.builder()
                .packageToScan(event.getName())
                .build());
        this.handle(ExtractValueObject.builder()
                .packageToScan(event.getName())
                .build());
        this.handle(ExtractDomainEvent.builder()
                .packageToScan(event.getName())
                .build());
        this.handle(ExtractDomainCommand.builder()
                .packageToScan(event.getName())
                .build());
    }

    public void apply(CoreConceptFound event) {
        this.handle(CheckConceptInteraction.builder()
                .className(event.getClassName())
                .build());
    }

    public void apply(InteractionBetweenConceptFound event) {
    }

    public void apply(ValueObjectFound event) {
        this.handle(CheckConceptInteraction.builder()
                .className(event.getClassName())
                .build());
    }

    public void apply(DomainEventFound event) {
        this.handle(CheckConceptInteraction.builder()
                .className(event.getClassName())
                .build());
    }

    public  void apply(DomainCommandFound event) {
        this.handle(CheckConceptInteraction.builder()
                .className(event.getClassName())
                .build());

    }

    public void apply(NameConflictBetweenCoreConcept event) {
    }

    public void apply(WrongUsageOfValueObjectFound event) {
    }
}
