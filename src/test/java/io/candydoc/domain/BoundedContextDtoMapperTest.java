package io.candydoc.domain;

import io.candydoc.domain.events.*;
import io.candydoc.infra.BoundedContextDtoMapper;
import io.candydoc.infra.model.*;
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
        //given
        eventList.add(BoundedContextFound.builder()
                .name("bounded context")
                .description("description").build());
    }

    @Test
    void generate_bounded_context_from_bounded_context_found_() {
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).contains(BoundedContextDto.builder()
                .name("bounded context")
                .description("description")
                .coreConcepts(List.of())
                .valueObjects(List.of())
                .domainEvents(List.of())
                .domainCommands(List.of())
                .errors(List.of())
                .build());
    }

    @Test
    void generate_core_concept_dto_from_core_concept_found() {
        //given
        eventList.add(CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConceptClass")
                .boundedContext("bounded context").build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("coreConcepts")
                .contains(CoreConceptDto.builder()
                        .name("core concept")
                        .description("core concept description")
                        .className("CoreConceptClass")
                        .errors(List.of())
                        .interactsWith(Set.of())
                        .build());
    }

    @Test
    void generate_domain_command_dto_from_domain_command_found() {
        //given
        eventList.add(DomainCommandFound.builder()
                .description("domain command description")
                .className("DomainCommandClass")
                .boundedContext("bounded context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("domainCommands")
                .contains(DomainCommandDto.builder()
                        .description("domain command description")
                        .className("DomainCommandClass")
                        .build());
    }

    @Test
    void generate_domain_event_dto_from_domain_event_found() {
        //given
        eventList.add(DomainEventFound.builder()
                .description("domain event description")
                .boundedContext("bounded context")
                .className("domainEventClass").build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("domainEvents")
                .contains(DomainEventDto.builder()
                        .description("domain event description")
                        .className("domainEventClass")
                        .build());
    }

    @Test
    void generate_value_object_dto_from_value_object_found() {
        //given
        eventList.add(ValueObjectFound.builder()
                .description("Value Object description")
                .className("ValueObjectClass")
                .boundedContext("bounded context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto)
                .flatExtracting("valueObjects")
                .contains(ValueObjectDto.builder()
                        .description("Value Object description")
                        .className("ValueObjectClass")
                        .errors(List.of())
                        .build());
    }

    @Test
    void generate_error_in_core_concept_dto() {
        //given
        eventList.add(CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConceptClass")
                .boundedContext("bounded context").build());
        eventList.add(NameConflictBetweenCoreConcept.builder()
                .conflictingCoreConcepts(List.of("CoreConceptClass"))
                .UsageError("Warning: Share same name with another core concept").build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto)
                .flatExtracting("coreConcepts")
                .flatExtracting("errors")
                .contains("Warning: Share same name with another core concept");
    }

    @Test
    void generate_error_in_value_object_dto() {
        //given
        eventList.add(ValueObjectFound.builder()
                .description("core concept description")
                .className("valueObjectClass")
                .boundedContext("bounded context").build());
        eventList.add(WrongUsageOfValueObjectFound.builder()
                .valueObject("valueObjectClass")
                .usageError("Warning: Value Object should only contain primitive type").build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto)
                .flatExtracting("valueObjects")
                .flatExtracting("errors")
                .contains("Warning: Value Object should only contain primitive type");
    }
}