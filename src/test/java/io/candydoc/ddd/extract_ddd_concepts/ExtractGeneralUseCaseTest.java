package io.candydoc.ddd.extract_ddd_concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.InteractionBetweenConceptFound;
import io.candydoc.ddd.model.ExtractionException;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class ExtractGeneralUseCaseTest {

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

    assertThat(occurredGenerationEvents).isNotEmpty();
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
    assertThat(extractionCaptor.getResult())
        .filteredOn(InteractionBetweenConceptFound.class::isInstance)
        .containsExactlyInAnyOrder(
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
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainEvent1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.Aggregate1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.CoreConcept2")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.DomainCommand1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package.ValueObject1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.bounded_context_two.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.sub_package.CoreConcept1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.shared_kernel.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package.ValueObject1")
                .build(),
            InteractionBetweenConceptFound.builder()
                .from("io.candydoc.sample.valid_bounded_contexts.shared_kernel.package-info")
                .with(
                    "io.candydoc.sample.valid_bounded_contexts.shared_kernel.sub_package.CoreConcept1")
                .build());
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
