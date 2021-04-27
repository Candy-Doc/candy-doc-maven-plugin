package io.candydoc.infra.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class BoundedContextDto {
    @NonNull
    String name;
    @NonNull
    String description;
    List<CoreConceptDto> coreConcepts;
    List<ValueObjectDto> valueObjects;
    List<DomainEventDto> domainEvents;

    public void addCoreConcept(CoreConceptDto concept) {
        coreConcepts.add(concept);
    }

    public void addDomainEvents(DomainEventDto event) { domainEvents.add(event);}

    public void addValueObject(ValueObjectDto object) {
        valueObjects.add(object);
    }
}
