package io.candydoc.ddd.extract_ddd_concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.shared_kernel.SharedKernelFound;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class ExtractSharedKernelUseCaseTest {
  private ExtractDDDConceptsUseCase extractDDDConceptsUseCase;
  private SaveDocumentationPort saveDocumentationPort;
  private DDDConceptsExtractionService dddConceptsExtractionService;

  private ResultCaptor<List<Event>> extractionCaptor = new ResultCaptor<>();

  @BeforeEach
  public void setUp() {
    saveDocumentationPort = mock(SaveDocumentationPort.class);
    dddConceptsExtractionService =
        spy(new DDDConceptsExtractionService(new ReflectionsConceptFinder(List.of("io.candydoc"))));
    extractDDDConceptsUseCase =
        new ExtractDDDConceptsUseCase(dddConceptsExtractionService, saveDocumentationPort);

    doAnswer(extractionCaptor).when(dddConceptsExtractionService).extract(any(Command.class));
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
    assertThat(extractionCaptor.getResult())
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
    assertThat(extractionCaptor.getResult())
        .filteredOn(CoreConceptFound.class::isInstance)
        .containsExactlyInAnyOrder(
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
    assertThat(extractionCaptor.getResult())
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
    assertThat(extractionCaptor.getResult())
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
