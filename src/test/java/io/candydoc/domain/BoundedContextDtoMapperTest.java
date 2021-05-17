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
    void generate_bounded_context_from_bounded_context_found() {
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
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded context")
                .build());
        eventList.add(InteractionBetweenConceptFound.builder()
                .from("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .withFullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .withSimpleName("CoreConcept2")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("coreConcepts")
                .contains(CoreConceptDto.builder()
                        .name("core concept")
                        .description("core concept description")
                        .errors(List.of())
                        .className("CoreConcept1")
                        .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                        .interactsWith(Set.of(InteractionDto.builder()
                                .simpleName("CoreConcept2")
                                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                                .build()))
                        .build());
    }

    @Test
    void generate_domain_command_dto_from_domain_command_found() {
        //given
        eventList.add(DomainCommandFound.builder()
                .description("domain command description")
                .className("DomainCommand1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("domainCommands")
                .contains(DomainCommandDto.builder()
                        .description("domain command description")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                        .className("DomainCommand1")
                        .build());
    }

    @Test
    void generate_domain_event_dto_from_domain_event_found() {
        // given
        eventList.add(DomainEventFound.builder()
                .description("domain event description")
                .className("DomainEvent1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded context")
                .build());
        // when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(dto).flatExtracting("domainEvents")
                .contains(DomainEventDto.builder()
                        .description("domain event description")
                        .className("DomainEvent1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                        .build());
    }

    @Test
    void generate_value_object_dto_from_value_object_found() {
        //given
        eventList.add(ValueObjectFound.builder()
                .description("Value Object description")
                .className("ValueObject1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("valueObjects")
                .contains(ValueObjectDto.builder()
                        .description("Value Object description")
                        .className("ValueObject1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .errors(List.of())
                        .build());
    }

    @Test
    void generate_error_in_core_concept_dto() {
        //given
        eventList.add(CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded context")
                .build());
        eventList.add(NameConflictBetweenCoreConcept.builder()
                .conflictingCoreConcepts(List.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"))
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
                .description("value object description")
                .className("ValueObject1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded context").build());
        eventList.add(WrongUsageOfValueObjectFound.builder()
                .valueObject("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .usageError("Warning: Value Object should only contain primitive type")
                .build());
        // when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(dto)
                .flatExtracting("valueObjects")
                .flatExtracting("errors")
                .contains("Warning: Value Object should only contain primitive type");
    }
}