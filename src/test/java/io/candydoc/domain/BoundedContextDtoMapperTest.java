package io.candydoc.domain;

import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.BoundedContextFound;
import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import io.candydoc.domain.events.NameConflictBetweenCoreConcepts;
import io.candydoc.domain.events.ValueObjectFound;
import io.candydoc.infra.BoundedContextDtoMapper;
import io.candydoc.infra.model.BoundedContextDto;
import io.candydoc.infra.model.ConceptDto;
import io.candydoc.infra.model.ConceptType;
import io.candydoc.infra.model.InteractionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class BoundedContextDtoMapperTest {

    private final List<DomainEvent> eventList = new LinkedList<>();
    private final String BOUNDED_CONTEXT_NAME = "bounded context";

    @BeforeEach
    public void setUp() {
        // given
        eventList.add(BoundedContextFound.builder()
            .name(BOUNDED_CONTEXT_NAME)
            .packageName("bounded.context")
            .description("description")
            .build());
    }

    @Test
    void generate_bounded_context_from_bounded_context_found() {
        // given
        eventList.add(BoundedContextFound.builder()
            .name("bounded context 2")
            .packageName("bounded.context2")
            .description("description 2")
            .build());

        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);

        // then
        Assertions.assertThat(boundedContextDtos)
            .containsExactlyInAnyOrder(
                BoundedContextDto.builder()
                    .name(BOUNDED_CONTEXT_NAME)
                    .packageName("bounded.context")
                    .description("description")
                    .build(),
                BoundedContextDto.builder()
                    .name("bounded context 2")
                    .packageName("bounded.context2")
                    .description("description 2")
                    .build());
    }

    @Test
    void generate_core_concept_dto_from_core_concept_found() {
        // given
        eventList.addAll(
            List.of(CoreConceptFound.builder()
                    .name("core concept 1")
                    .description("core concept description 1")
                    .className("CoreConcept1")
                    .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                    .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                    .boundedContext("bounded.context")
                    .build(),
                CoreConceptFound.builder()
                    .name("core concept 2")
                    .description("core concept description 2")
                    .className("CoreConcept2")
                    .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                    .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                    .boundedContext("bounded.context")
                    .build(),
                InteractionBetweenConceptFound.builder()
                    .from("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                    .with("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                    .build()));
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.CORE_CONCEPT))
            .containsExactlyInAnyOrder(
                ConceptDto.builder()
                    .name("core concept 1")
                    .description("core concept description 1")
                    .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                    .type(ConceptType.CORE_CONCEPT)
                    .interactsWith(Set.of(InteractionDto.builder()
                        .simpleName("core concept 2")
                        .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                        .build()))
                    .build(),
                ConceptDto.builder()
                    .name("core concept 2")
                    .description("core concept description 2")
                    .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                    .type(ConceptType.CORE_CONCEPT)
                    .interactsWith(Set.of(InteractionDto.builder()
                        .simpleName("core concept 1")
                        .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                        .build()))
                    .build());
    }

    @Test
    void generate_domain_command_dto_from_domain_command_found() {
        // given
        eventList.add(DomainCommandFound.builder()
            .description("domain command description")
            .className("DomainCommand1")
            .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.DOMAIN_COMMAND))
            .contains(ConceptDto.builder()
                .description("domain command description")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                .type(ConceptType.DOMAIN_COMMAND)
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
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.DOMAIN_EVENT))
            .contains(ConceptDto.builder()
                .description("domain event description")
                .name("DomainEvent1")
                .type(ConceptType.DOMAIN_EVENT)
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                .build());
    }

    @Test
    void generate_value_object_dto_from_value_object_found() {
        // given
        eventList.add(ValueObjectFound.builder()
            .description("Value Object description")
            .className("ValueObject1")
            .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.VALUE_OBJECT))
            .contains(ConceptDto.builder()
                .description("Value Object description")
                .name("ValueObject1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .type(ConceptType.VALUE_OBJECT)
                .errors(List.of())
                .build());
    }

    @Test
    void generate_aggregate_in_dto_mapper() {
        // given
        eventList.add(AggregateFound.builder()
            .name("aggregate 1")
            .description("Aggregate for Bounded context 1")
            .className("Aggregate1")
            .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.AGGREGATE))
            .contains(ConceptDto.builder()
                .name("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .errors(List.of())
                .type(ConceptType.AGGREGATE)
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                .interactsWith(Set.of())
                .build());
    }

    @Test
    void generate_error_in_core_concept_dto() {
        // given
        eventList.addAll(List.of(
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description 2")
                .className("CoreConcept2")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            NameConflictBetweenCoreConcepts.builder()
                .coreConceptClassNames(List.of(
                    "candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1",
                    "candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2"
                ))
                .build()));
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.CORE_CONCEPT))
            .flatMap(ConceptDto::getErrors)
            .contains("Share same name with another core concept");
    }

    @Test
    void generate_error_in_value_object_dto() {
        // given
        eventList.addAll(List.of(
            ValueObjectFound.builder()
                .description("value object description")
                .className("ValueObject1")
                .fullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded.context")
                .build(),
            ConceptRuleViolated.builder()
                .conceptFullName("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .reason("Warning: Value Object should only contain primitive type")
                .build()));
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.VALUE_OBJECT))
            .flatMap(ConceptDto::getErrors)
            .contains("Warning: Value Object should only contain primitive type");
    }

    @Test
    void generate_error_core_concept_interacting_with_aggregate() {
        // given
        eventList.addAll(List.of(
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            ConceptRuleViolated.builder()
                .conceptFullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .reason("CoreConcept interact with candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1 Aggregates.")
                .build()));
        // when
        List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
        // then
        Assertions.assertThat(boundedContextDtos)
            .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
            .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.CORE_CONCEPT))
            .flatMap(ConceptDto::getErrors)
            .contains("CoreConcept interact with candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1 Aggregates.");
    }
}
