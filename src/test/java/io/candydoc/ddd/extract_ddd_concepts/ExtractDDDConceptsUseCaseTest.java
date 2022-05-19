package io.candydoc.ddd.extract_ddd_concepts;

import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.AggregateFound;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.domain_command.DomainCommandFound;
import io.candydoc.ddd.domain_event.DomainEventFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.model.ExtractionException;
import io.candydoc.ddd.shared_kernel.SharedKernelFound;
import io.candydoc.ddd.value_object.ValueObjectFound;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class ExtractDDDConceptsUseCaseTest {

  private ExtractDDDConceptsUseCase extractDDDConceptsUseCase;
  private SaveDocumentationPort saveDocumentationPort;
  private DDDConceptsExtractionService dddConceptsExtractionService;

  private ResultCaptor<List<Event>> extractionCaptor = new ResultCaptor<>();

  @BeforeEach
  public void setUp() {
    saveDocumentationPort = mock(SaveDocumentationPort.class);
    dddConceptsExtractionService =
        spy(new DDDConceptsExtractionService(new ReflectionsConceptFinder()));
    extractDDDConceptsUseCase =
        new ExtractDDDConceptsUseCase(dddConceptsExtractionService, saveDocumentationPort);

    doAnswer(extractionCaptor).when(dddConceptsExtractionService).extract(any(Command.class));
  }

  @Test
  void package_to_scan_is_not_provided() {
    // given
    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packagesToScan(List.of()).build();

    // when then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(PackageToScanMissing.class)
        .hasMessage("Missing parameters for 'packageToScan'. Check your pom configuration.");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "    ", "  \t  "})
  void blank_package_to_scan_is_not_allowed(String packageToScan) {
    // given
    ExtractDDDConcepts command = ExtractDDDConcepts.builder().packageToScan(packageToScan).build();

    // when then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(PackageToScanMissing.class)
        .hasMessage(
            "Blank packageToScan not allowed for 'packagesToScan'. Check your pom configuration");
  }

  @Test
  void generated_documentation_from_multiple_packages() throws ExtractionException, IOException {
    // given
    ArgumentCaptor<List<Event>> resultCaptor = ArgumentCaptor.forClass(List.class);

    // when
    extractDDDConceptsUseCase.execute(
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .packageToScan("io.candydoc.sample.second_valid_bounded_contexts")
            .build());

    // then
    verify(saveDocumentationPort, times(1)).save(resultCaptor.capture());

    List<Event> occurredGenerationEvents = resultCaptor.getValue();

    Assertions.assertThat(occurredGenerationEvents).isNotEmpty();
  }

  @Test
  void no_bounded_context_nor_shared_kernel_in_the_package_to_scan() {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder().packageToScan("wrong.package.to.scan").build();

    // then
    Assertions.assertThatThrownBy(() -> extractDDDConceptsUseCase.execute(command))
        .isInstanceOf(NoBoundedContextNorSharedKernelFound.class)
        .hasMessage(
            "No bounded context nor shared kernel has been found in this packages :"
                + " '[wrong.package.to.scan]'.");
  }

  @Test
  void find_bounded_contexts_inside_given_packages() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            BoundedContextFound.builder()
                .simpleName("bounded_context_one")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .description("description of bounded context 1")
                .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            BoundedContextFound.builder()
                .simpleName("bounded_context_two")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.package-info")
                .description("description of bounded context 2")
                .packageName("io.candydoc.sample.valid_bounded_contexts.bounded_context_two")
                .build());
  }

  @Test
  void find_core_concepts_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            CoreConceptFound.builder()
                .simpleName("name of core concept 1 of bounded context 1")
                .description("description of core concept 1 of bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build(),
            CoreConceptFound.builder()
                .simpleName("name of core concept 2 of bounded context 1")
                .description("description of core concept 2 of bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_value_objects_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ValueObjectFound.builder()
                .description("description of value object 1 of bounded context 1")
                .simpleName("ValueObject1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void value_object_should_only_contain_primitive_types() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ConceptRuleViolated.builder()
                .conceptName(
                    "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package.ValueObject")
                .reason("Value Object should only contain primitive types")
                .build());
  }

  @Test
  void find_domain_events_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            DomainEventFound.builder()
                .description("domain event 1 of boundedcontext 1")
                .simpleName("DomainEvent1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainEvent1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_domain_commands_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            DomainCommandFound.builder()
                .description("Domain Command for Bounded context 1")
                .simpleName("DomainCommand1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainCommand1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_aggregates_inside_bounded_contexts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            AggregateFound.builder()
                .simpleName("aggregate 1")
                .description("Aggregate for Bounded context 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.Aggregate1")
                .packageName(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.bounded_context_one")
                .build());
  }

  @Test
  void find_interaction_between_two_different_concepts() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            InteractionBetweenConceptFound.builder()
                .from(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2")
                .build());
  }

  @Test
  void find_shared_kernel_inside_given_packages() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            SharedKernelFound.builder()
                .simpleName("shared_kernel_one")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.shared_kernel.package-info")
                .description("description of shared kernel")
                .packageName("io.candydoc.sample.valid_bounded_contexts.shared_kernel")
                .build());
  }

  @Test
  void find_core_concepts_inside_shared_kernel() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts.shared_kernel")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            CoreConceptFound.builder()
                .simpleName("name of core concept 1 of shared kernel 1")
                .description("description of core concept 1 of shared kernel 1")
                .canonicalName(
                    "io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package.CoreConcept1")
                .packageName("io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package")
                .boundedContext("io.candydoc.sample.valid_bounded_contexts.shared_kernel")
                .build());
  }

  @ParameterizedTest
  @MethodSource("forbidden_concepts_in_bounded_context_examples")
  void forbidden_concepts_in_bounded_context(String conceptName, String ruleViolatedReason)
      throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.wrong_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ConceptRuleViolated.builder()
                .conceptName(conceptName)
                .reason(ruleViolatedReason)
                .build());
  }

  public static Stream<Arguments> forbidden_concepts_in_bounded_context_examples() {
    return Stream.of(
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.Aggregate1",
            "Shared kernel can not have aggregate."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.DomainCommand1",
            "Shared kernel can not have domain command."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.DomainEvent1",
            "Shared kernel can not have domain event."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.bounded_context.inner_shared_kernel",
            "Shared kernel shared_kernel_three is not allowed in a bounded context."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.bounded_context.inner_bounded_context",
            "Bounded context bounded_context_two is not allowed in another bounded context."));
  }

  @ParameterizedTest
  @MethodSource("forbidden_concepts_in_shared_kernel_examples")
  void forbidden_concepts_in_shared_kernel(String conceptName, String ruleViolatedReason)
      throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.wrong_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            ConceptRuleViolated.builder()
                .conceptName(conceptName)
                .reason(ruleViolatedReason)
                .build());
  }

  public static Stream<Arguments> forbidden_concepts_in_shared_kernel_examples() {
    return Stream.of(
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.Aggregate1",
            "Shared kernel can not have aggregate."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.DomainCommand1",
            "Shared kernel can not have domain command."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.sub_package.DomainEvent1",
            "Shared kernel can not have domain event."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.inner_bounded_context",
            "Bounded context bounded_context_three is not allowed in a shared kernel."),
        Arguments.of(
            "io.candydoc.sample.wrong_bounded_contexts.shared_kernel.inner_shared_kernel",
            "Shared kernel shared_kernel_two is not allowed in another shared kernel."));
  }

  @ParameterizedTest
  @MethodSource("allowed_interactions_with_bounded_context_examples")
  void find_interactions_with_bounded_context(String conceptName) throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            InteractionBetweenConceptFound.builder()
                .with(conceptName)
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .build());
  }

  public static Stream<Arguments> allowed_interactions_with_bounded_context_examples() {
    return Stream.of(
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.Aggregate1"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainCommand1"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainEvent1"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1"));
  }

  @ParameterizedTest
  @MethodSource("allowed_interactions_with_shared_kernel_examples")
  void find_interactions_with_shared_kernel(String conceptName) throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.valid_bounded_contexts")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    Assertions.assertThat(extractionCaptor.getResult())
        .contains(
            InteractionBetweenConceptFound.builder()
                .with(conceptName)
                .from("io.candydoc.sample.valid_bounded_contexts.shared_kernel.package-info")
                .build());
  }

  public static Stream<Arguments> allowed_interactions_with_shared_kernel_examples() {
    return Stream.of(
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package.CoreConcept1"),
        Arguments.of(
            "io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package.ValueObject1"));
  }

  public class ResultCaptor<T> implements Answer {
    private T result = null;

    public T getResult() {
      return result;
    }

    @Override
    public T answer(InvocationOnMock invocationOnMock) throws Throwable {
      result = (T) invocationOnMock.callRealMethod();
      return result;
    }
  }
}
