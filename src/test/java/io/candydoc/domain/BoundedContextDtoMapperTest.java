package io.candydoc.domain;

import io.candydoc.domain.events.*;
import io.candydoc.infra.BoundedContextDtoMapper;
import io.candydoc.infra.model.BoundedContextDto;
import io.candydoc.infra.model.CoreConceptDto;
import io.candydoc.infra.model.DomainEventDto;
import io.candydoc.infra.model.ValueObjectDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class BoundedContextDtoMapperTest {

    private BoundedContextDtoMapper boundedContextDtoMapper;
    List<DomainEvent> eventList = new LinkedList<>();

    @BeforeEach
    public void setUp() {
        boundedContextDtoMapper = new BoundedContextDtoMapper();
        eventList.add(BoundedContextFound.builder()
                .name("bounded context")
                .description("description").build());
    }

    @Test
    void generate_bounded_context_from_bounded_context_found_() {
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        Assertions.assertThat(dto).contains(BoundedContextDto.builder()
                .name("bounded context")
                .description("description")
                .coreConcepts(List.of())
                .valueObjects(List.of())
                .domainEvents(List.of())
                .build());
    }

    @Test
    void generate_core_concept_dto_from_core_concept_found() {
        eventList.add(CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConceptClass")
                .boundedContext("bounded context").build());
        Assertions.assertThat(boundedContextDtoMapper.map(eventList)).flatExtracting("coreConcepts")
                .contains(CoreConceptDto.builder()
                        .name("core concept")
                        .description("core concept description")
                        .className("CoreConceptClass")
                        .interactsWith(Set.of())
                        .build());
    }

    @Test
    void generate_domain_event_dto_from_domain_event_found() {
        eventList.add(DomainEventFound.builder()
                .description("domain event description")
                .boundedContext("bounded context")
                .className("domainEventClass").build());
        Assertions.assertThat(boundedContextDtoMapper.map(eventList)).flatExtracting("domainEvents")
                .contains(DomainEventDto.builder()
                        .description("domain event description")
                        .className("domainEventClass")
                        .build());
    }

    @Test
    void generate_value_object_dto_from_value_object_found() {
        eventList.add(ValueObjectFound.builder()
                .description("Value Object description")
                .className("ValueObjectClass")
                .boundedContext("bounded context")
                .build());
        Assertions.assertThat(boundedContextDtoMapper.map(eventList)).flatExtracting("valueObjects").contains(ValueObjectDto.builder()
                        .description("Value Object description")
                        .className("ValueObjectClass")
                        .build());
    }
}