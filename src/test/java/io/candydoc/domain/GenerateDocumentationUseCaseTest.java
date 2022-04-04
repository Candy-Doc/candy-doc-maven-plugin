package io.candydoc.domain;

import static org.mockito.Mockito.*;

import io.candydoc.domain.command.ExtractDDDConcepts;
import io.candydoc.domain.events.*;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.DomainException;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.extractor.ConceptFinder;
import io.candydoc.domain.extractor.DDDConceptExtractor;
import java.io.IOException;
import java.util.List;

import io.candydoc.domain.extractor.ReflectionsConceptFinder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GenerateDocumentationUseCaseTest {

  private GenerateDocumentationUseCase generateDocumentationUseCase;
  private SaveDocumentationPort saveDocumentationPort;
  private DDDConceptExtractor DDDConceptExtractor;
  private ConceptFinder conceptFinder;

  @BeforeEach
  public void setUp() {
    saveDocumentationPort = mock(SaveDocumentationPort.class);
    conceptFinder = new ReflectionsConceptFinder();
    generateDocumentationUseCase = new GenerateDocumentationUseCase(saveDocumentationPort, conceptFinder);
    DDDConceptExtractor = new DDDConceptExtractor(conceptFinder);
  }

  @Test
  void package_to_scan_is_not_provided() {
    // given
    List<String> givenPackages = List.of();

    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packagesToScan(givenPackages).build();

    // when then
    Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(command))
        .isInstanceOf(DocumentationGenerationFailed.class)
        .hasMessage("Missing parameters for 'packageToScan'. Check your pom configuration.");
  }

  @Test
  void package_to_scan_report_empty_string() {
    // given
    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packageToScan("").build();

    // when then
    Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(command))
        .isInstanceOf(DocumentationGenerationFailed.class)
        .hasMessage("Empty parameters for 'packagesToScan'. Check your pom configuration");
  }

  @Test
  void generated_documentation_from_multiple_packages() throws DomainException, IOException {
    // given
    ArgumentCaptor<List<DomainEvent>> resultCaptor = ArgumentCaptor.forClass(List.class);

    // when
    generateDocumentationUseCase.execute(
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .packageToScan("candydoc.sample.second_valid_bounded_contexts")
            .build());

    // then
    verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());

    List<DomainEvent> occurredGenerationEvents = resultCaptor.getValue();

    Assertions.assertThat(occurredGenerationEvents).isNotEmpty();
  }

  @Test
  void package_to_scan_is_not_following_ddd() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder().packageToScan("wrong.package.to.scan").build();

    // then
    Assertions.assertThatThrownBy(() -> generateDocumentationUseCase.execute(command))
        .isInstanceOf(NoBoundedContextFound.class)
        .hasMessage("No bounded context has been found in the package : 'wrong.package.to.scan'.");
  }

  @Test
  void find_bounded_contexts_inside_given_packages() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);

    // then
    Assertions.assertThat(actualEvents)
        .contains(
            BoundedContextFound.builder()
                .name("bounded_context_one")
                .description("description of bounded context 1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            BoundedContextFound.builder()
                .name("bounded_context_two")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_two")
                .description("description of bounded context 2")
                .build());
  }

  @Test
  void find_core_concepts_inside_bounded_contexts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);

    // then
    Assertions.assertThat(actualEvents)
        .contains(
            CoreConceptFound.builder()
                .name("name of core concept 1 of bounded context 1")
                .description("description of core concept 1 of bounded context 1")
                .className(
                    "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            CoreConceptFound.builder()
                .name("name of core concept 2 of bounded context 1")
                .description("description of core concept 2 of bounded context 1")
                .className(
                    "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_value_objects_inside_bounded_contexts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();
    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
    // then
    Assertions.assertThat(actualEvents)
        .contains(
            ValueObjectFound.builder()
                .description("description of value object 1 of bounded context 1")
                .name("ValueObject1")
                .className(
                    "candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void value_object_shoud_only_contain_primitive_types() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
            .build();
    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
    // then
    Assertions.assertThat(actualEvents)
        .contains(
            ConceptRuleViolated.builder()
                .className(
                    "candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.ValueObject")
                .reason("Value Object should only contain primitive types")
                .build());
  }

  @Test
  void find_domain_events_inside_bounded_contexts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();
    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);
    // then
    Assertions.assertThat(actualEvents)
        .contains(
            DomainEventFound.builder()
                .description("domain event 1 of boundedcontext 1")
                .name("DomainEvent1")
                .className(
                    "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_domain_commands_inside_bounded_contexts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);

    // then
    Assertions.assertThat(actualEvents)
        .contains(
            DomainCommandFound.builder()
                .description("Domain Command for Bounded context 1")
                .name("DomainCommand1")
                .className(
                    "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainCommand1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_aggregates_inside_bounded_contexts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);

    // then
    Assertions.assertThat(actualEvents)
        .contains(
            AggregateFound.builder()
                .name("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.Aggregate1")
                .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_interaction_between_two_different_concepts() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    List<DomainEvent> actualEvents = DDDConceptExtractor.extract(command);

    // then
    Assertions.assertThat(actualEvents)
        .contains(
            InteractionBetweenConceptFound.builder()
                .from("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
                .with("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
                .build());
  }
}
