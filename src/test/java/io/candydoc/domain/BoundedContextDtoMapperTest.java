package io.candydoc.domain;

import io.candydoc.domain.events.*;
import io.candydoc.infra.BoundedContextDtoMapper;
import io.candydoc.infra.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class BoundedContextDtoMapperTest {

    private BoundedContextDtoMapper boundedContextDtoMapper;
    List<DomainEvent> eventList = new LinkedList<>();
    Map<BoundedContextDto.ConceptType, List<ConceptDto>> conceptsMap;

    @BeforeEach
    public void setUp() {
        boundedContextDtoMapper = new BoundedContextDtoMapper();
        //given
        eventList.add(BoundedContextFound.builder()
                .name("bounded context")
                .packageName("bounded.context")
                .description("description").build());
    }

    @Test
    void generate_bounded_context_from_bounded_context_found() {
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).contains(BoundedContextDto.builder()
                .name("bounded context")
                .packageName("bounded.context")
                .description("description")
                .conceptsMap(Map.of())
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
                .boundedContext("bounded.context")
                .build());
        eventList.add(CoreConceptFound.builder()
                .name("core concept2")
                .description("core concept description")
                .className("CoreConcept2")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build());
        eventList.add(InteractionBetweenConceptFound.builder()
                .from("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .withFullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("coreConcepts")
                .contains(ConceptDto.builder()
                        .name("core concept")
                        .description("core concept description")
                        .errors(List.of())
                        .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                        .conceptType(BoundedContextDto.ConceptType.CORE_CONCEPT)
                        .interactsWith(Set.of(InteractionDto.builder()
                                .simpleName("core concept2")
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
                .boundedContext("bounded.context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("domainCommands")
                .contains(ConceptDto.builder()
                        .description("domain command description")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                        .conceptType(BoundedContextDto.ConceptType.DOMAIN_COMMAND)
                        .name("DomainCommand1")
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
                .boundedContext("bounded.context")
                .build());
        // when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(dto).flatExtracting("domainEvents")
                .contains(ConceptDto.builder()
                        .description("domain event description")
                        .name("DomainEvent1")
                        .conceptType(BoundedContextDto.ConceptType.DOMAIN_EVENT)
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
                .boundedContext("bounded.context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("valueObjects")
                .contains(ConceptDto.builder()
                        .description("Value Object description")
                        .name("ValueObject1")
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                        .conceptType(BoundedContextDto.ConceptType.VALUE_OBJECT)
                        .errors(List.of())
                        .build());
    }

    @Test
    void generate_aggregate_in_dto_mapper() {
        //given
        eventList.add(AggregateFound.builder()
                .name("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .className("Aggregate1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded.context")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto).flatExtracting("aggregates")
                .contains(ConceptDto.builder()
                        .name("aggregate 1")
                        .description("Aggregate for Bounded context 1")
                        .errors(List.of())
                        .conceptType(BoundedContextDto.ConceptType.AGGREGATE)
                        .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                        .interactsWith(Set.of())
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
                .boundedContext("bounded.context")
                .build());
        eventList.add(NameConflictBetweenCoreConcept.builder()
                .conflictingCoreConcepts(List.of("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"))
                .UsageError("Warning: Share same name with another core concept").build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto.stream().map(bc -> bc.getConceptsMap()
                .get(BoundedContextDto.ConceptType.CORE_CONCEPT))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).stream()
                .map(coreConcept -> coreConcept.getErrors())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()))
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
                .boundedContext("bounded.context").build());
        eventList.add(ConceptRuleViolated.builder()
                .conceptFullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .reason("Warning: Value Object should only contain primitive type")
                .build());
        // when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(dto.stream().map(bc -> bc.getConceptsMap()
                .get(BoundedContextDto.ConceptType.VALUE_OBJECT))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).stream()
                .map(coreConcept -> coreConcept.getErrors())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()))
                .contains("Warning: Value Object should only contain primitive type");
    }

    @Test
    void generate_error_core_concept_interacting_with_aggregate() {
        //given
        eventList.add(CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build());
        eventList.add(ConceptRuleViolated.builder()
                .conceptFullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .reason("CoreConcept interact with candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1 Aggregates.")
                .build());
        //when
        List<BoundedContextDto> dto = boundedContextDtoMapper.map(eventList);
        //then
        Assertions.assertThat(dto.stream().map(bc -> bc.getConceptsMap()
                .get(BoundedContextDto.ConceptType.CORE_CONCEPT))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).stream()
                .map(coreConcept -> coreConcept.getErrors())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()))
                .contains("CoreConcept interact with candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1 Aggregates.");
    }
}