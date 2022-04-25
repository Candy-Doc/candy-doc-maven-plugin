package io.candydoc.plugin.model;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.core_concept.NameConflictBetweenCoreConcepts;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.value_object.ValueObjectFound;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundedContextDtoMapperTest {

  private final List<Event> eventList = new LinkedList<>();
  private final String BOUNDED_CONTEXT_NAME = "bounded context";

  @BeforeEach
  public void setUp() {
    // given
    eventList.add(
        BoundedContextFound.builder()
            .name(BOUNDED_CONTEXT_NAME)
            .packageName("bounded.context")
            .description("description")
            .build());
  }

  @Test
  void generate_bounded_context_from_bounded_context_found() {
    // given
    eventList.add(
        BoundedContextFound.builder()
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
        List.of(
            CoreConceptFound.builder()
                .name("core concept 1")
                .description("core concept description 1")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            CoreConceptFound.builder()
                .name("core concept 2")
                .description("core concept description 2")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .with("io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
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
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .type(ConceptType.CORE_CONCEPT)
                .interactsWith(
                    Set.of(
                        InteractionDto.builder()
                            .name("core concept 2")
                            .className(
                                "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                            .build()))
                .build(),
            ConceptDto.builder()
                .name("core concept 2")
                .description("core concept description 2")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .type(ConceptType.CORE_CONCEPT)
                .interactsWith(
                    Set.of(
                        InteractionDto.builder()
                            .name("core concept 1")
                            .className(
                                "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                            .build()))
                .build());
  }

  @Test
  void generate_domain_command_dto_from_domain_command_found() {
    // given
    eventList.add(
        DomainCommandFound.builder()
            .description("domain command description")
            .name("DomainCommand1")
            .className(
                "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
            .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
    // when
    List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
    // then
    Assertions.assertThat(boundedContextDtos)
        .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
        .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.DOMAIN_COMMAND))
        .contains(
            ConceptDto.builder()
                .description("domain command description")
                .className(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                .type(ConceptType.DOMAIN_COMMAND)
                .name("DomainCommand1")
                .build());
  }

  @Test
  void generate_domain_event_dto_from_domain_event_found() {
    // given
    eventList.add(
        DomainEventFound.builder()
            .description("domain event description")
            .name("DomainEvent1")
            .className("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
            .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
    // when
    List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
    // then
    Assertions.assertThat(boundedContextDtos)
        .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
        .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.DOMAIN_EVENT))
        .contains(
            ConceptDto.builder()
                .description("domain event description")
                .name("DomainEvent1")
                .type(ConceptType.DOMAIN_EVENT)
                .className(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                .build());
  }

  @Test
  void generate_value_object_dto_from_value_object_found() {
    // given
    eventList.add(
        ValueObjectFound.builder()
            .description("Value Object description")
            .name("ValueObject1")
            .className("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
            .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
    // when
    List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
    // then
    Assertions.assertThat(boundedContextDtos)
        .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
        .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.VALUE_OBJECT))
        .contains(
            ConceptDto.builder()
                .description("Value Object description")
                .name("ValueObject1")
                .className(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .type(ConceptType.VALUE_OBJECT)
                .errors(List.of())
                .build());
  }

  @Test
  void generate_aggregate_in_dto_mapper() {
    // given
    eventList.add(
        AggregateFound.builder()
            .name("aggregate 1")
            .description("Aggregate for Bounded context 1")
            .className("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
            .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("bounded.context")
            .build());
    // when
    List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
    // then
    Assertions.assertThat(boundedContextDtos)
        .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
        .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.AGGREGATE))
        .contains(
            ConceptDto.builder()
                .name("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .errors(List.of())
                .type(ConceptType.AGGREGATE)
                .className(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                .interactsWith(Set.of())
                .build());
  }

  @Test
  void generate_error_in_core_concept_dto() {
    // given
    eventList.addAll(
        List.of(
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description 2")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            NameConflictBetweenCoreConcepts.builder()
                .coreConcepts(
                    List.of(
                        "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1",
                        "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept2"))
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
    eventList.addAll(
        List.of(
            ValueObjectFound.builder()
                .description("value object description")
                .name("ValueObject1")
                .className(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("bounded.context")
                .build(),
            ConceptRuleViolated.builder()
                .conceptName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
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
    eventList.addAll(
        List.of(
            CoreConceptFound.builder()
                .name("core concept")
                .description("core concept description")
                .className(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .boundedContext("bounded.context")
                .build(),
            ConceptRuleViolated.builder()
                .conceptName(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .reason(
                    "CoreConcept interact with"
                        + " candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"
                        + " Aggregates.")
                .build()));
    // when
    List<BoundedContextDto> boundedContextDtos = BoundedContextDtoMapper.map(eventList);
    // then
    Assertions.assertThat(boundedContextDtos)
        .filteredOn(boundedContextDto -> boundedContextDto.getName().equals(BOUNDED_CONTEXT_NAME))
        .flatMap(boundedContextDto -> boundedContextDto.getConcepts(ConceptType.CORE_CONCEPT))
        .flatMap(ConceptDto::getErrors)
        .contains(
            "CoreConcept interact with"
                + " candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1"
                + " Aggregates.");
  }
}
