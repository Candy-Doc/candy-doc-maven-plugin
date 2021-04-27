package io.candydoc.infra;

import io.candydoc.domain.events.*;
import io.candydoc.infra.model.BoundedContextDto;
import io.candydoc.infra.model.CoreConceptDto;
import io.candydoc.infra.model.DomainEventDto;
import io.candydoc.infra.model.ValueObjectDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BoundedContextDtoMapper {

    private class DtoVisitor implements DomainEvent.Visitor{
        List<BoundedContextDto> boundedContextDtos;

        DtoVisitor(List<BoundedContextDto> dtos) {
            boundedContextDtos = dtos;
        }

        public void apply(BoundedContextFound event) {
            boundedContextDtos.add(BoundedContextDto.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .coreConcepts(new LinkedList<>())
                    .valueObjects(new LinkedList<>())
                    .domainEvents(new LinkedList<>())
                    .build());
        }

        public void apply(CoreConceptFound event) {
            CoreConceptDto concept = CoreConceptDto.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .className(event.getClassName() )
                    .interactsWith(new HashSet<>())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addCoreConcept(concept));
        }

        public void apply(DomainEventFound event) {
            DomainEventDto domainEventDto = DomainEventDto.builder()
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addDomainEvents(domainEventDto));
        }

        public void apply(InteractionBetweenConceptFound event) {
            boundedContextDtos.stream()
                    .map(BoundedContextDto::getCoreConcepts)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).stream()
                    .filter(coreConceptDto -> coreConceptDto.getClassName().equals(event.getFrom()))
                    .forEach(coreConceptDto -> coreConceptDto.addInteractsWith(event.getWith()));
        }

        public void apply(ValueObjectFound event) {
            ValueObjectDto object = ValueObjectDto.builder()
                    .description(event.getDescription())
                    .className(event.getClassName())
                    .build();
            boundedContextDtos.stream()
                    .filter(boundedContext -> boundedContext.getName().equals(event.getBoundedContext()))
                    .forEach(boundedContext -> boundedContext.addValueObject(object));
        }
    }

    public List<BoundedContextDto> map(List<DomainEvent> domainEvents) {
        List<BoundedContextDto> boundedContextDtos = new LinkedList<>();
        DtoVisitor visitor = new DtoVisitor(boundedContextDtos);
        domainEvents.stream().forEach(domainEvent -> {
            domainEvent.accept(visitor);
        });
        return List.copyOf(boundedContextDtos);
    }

}
