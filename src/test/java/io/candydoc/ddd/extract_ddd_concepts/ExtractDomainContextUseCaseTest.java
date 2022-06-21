package io.candydoc.ddd.extract_ddd_concepts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.candydoc.ddd.Command;
import io.candydoc.ddd.Event;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class ExtractDomainContextUseCaseTest {
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
