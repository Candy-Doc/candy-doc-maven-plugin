package io.candydoc.ddd.extract_ddd_concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.ConceptRuleViolated;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class ExtractValueObjectUseCaseTest {
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
  void value_object_should_only_contain_primitive_types() throws IOException {
    // given
    ExtractDDDConcepts command =
        ExtractDDDConcepts.builder()
            .packageToScan("io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects")
            .build();

    // when
    extractDDDConceptsUseCase.execute(command);

    // then
    assertThat(extractionCaptor.getResult())
        .filteredOn(ConceptRuleViolated.class::isInstance)
        .containsExactlyInAnyOrder(
            ConceptRuleViolated.builder()
                .conceptName(
                    "io.candydoc.sample.bounded_context_for_wrong_usage_of_value_objects.sub_package.ValueObject")
                .reason("Value Object should only contain primitive types")
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
