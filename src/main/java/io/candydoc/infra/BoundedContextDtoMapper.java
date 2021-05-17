package io.candydoc.infra;

import io.candydoc.domain.events.*;
import io.candydoc.infra.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BoundedContextDtoMapper {

    private class DtoVisitor implements DomainEvent.Visitor {
        List<BoundedContextDto> boundedContextDtos;

        DtoVisitor(List<BoundedContextDto> dtos) {
            boundedContextDtos = dtos;
        }

        public void apply(NameConflictBetweenCoreConcept event) {
            event.getConflictingCoreConcepts().stream()
                    .forEach(conflictingClasses -> boundedContextDtos.stream()
                            .map(BoundedContextDto::getCoreConcepts)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
                            .stream()
                            .filter(coreConceptDto -> coreConceptDto.getFullName().equals(conflictingClasses))
                            .forEach(coreConceptDto -> coreConceptDto.addError(event.getUsageError())));
        }

        public void apply(WrongUsageOfValueObjectFound event) {
            boundedContextDtos.stream()
                    .map(BoundedContextDto::getValueObjects)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).stream()
                    .filter(valueObjectDto -> valueObjectDto.getFullName().equals(event.getValueObject()))
                    .forEach(valueObjectDto -> valueObjectDto.addError(event.getUsageError()));
        }

        public void apply(DomainCommandFound event) {
            DomainCommandDto command = DomainCommandDto.builder()
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .fullName(event.getFullName())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addDomainCommand(command));
        }

        public void apply(BoundedContextFound event) {
            boundedContextDtos.add(BoundedContextDto.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .coreConcepts(new LinkedList<>())
                    .valueObjects(new LinkedList<>())
                    .domainEvents(new LinkedList<>())
                    .domainCommands(new LinkedList<>())
                    .errors(new LinkedList<>())
                    .build());
        }

        public void apply(CoreConceptFound event) {
            CoreConceptDto concept = CoreConceptDto.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .fullName(event.getFullName())
                    .interactsWith(new HashSet<>())
                    .errors(new LinkedList<>())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addCoreConcept(concept));
        }

        public void apply(DomainEventFound event) {
            DomainEventDto domainEventDto = DomainEventDto.builder()
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .fullName(event.getFullName())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addDomainEvent(domainEventDto));
        }

        public void apply(InteractionBetweenConceptFound event) {
            boundedContextDtos.stream()
                    .map(BoundedContextDto::getCoreConcepts)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).stream()
                    .filter(coreConceptDto -> coreConceptDto.getFullName().equals(event.getFrom()))
                    .forEach(coreConceptDto -> coreConceptDto.addInteractsWith(InteractionDto.builder()
                            .fullName(event.getWithFullName())
                            .simpleName(event.getWithSimpleName())
                            .build()));
        }

        public void apply(ValueObjectFound event) {
            ValueObjectDto object = ValueObjectDto.builder()
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .errors(new LinkedList<>())
                    .fullName(event.getFullName())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addValueObject(object));
        }
    }

    public List<BoundedContextDto> map(List<DomainEvent> domainEvents) {
        List<BoundedContextDto> boundedContextDtos = new LinkedList<>();
        DtoVisitor visitor = new DtoVisitor(boundedContextDtos);
        domainEvents.forEach(domainEvent -> domainEvent.accept(visitor));
        return List.copyOf(boundedContextDtos);
    }
}