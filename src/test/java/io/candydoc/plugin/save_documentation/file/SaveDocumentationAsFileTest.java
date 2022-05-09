package io.candydoc.plugin.save_documentation.file;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.core_concept.CoreConceptFound;
import io.candydoc.plugin.model.BoundedContextDto;
import io.candydoc.plugin.model.ConceptDto;
import io.candydoc.plugin.model.ConceptType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SaveDocumentationAsFileTest {

  private SaveDocumentationAsFile saveDocumentationAsFile;
  private ArgumentCaptor<List<BoundedContextDto>> boundedContextsCaptor;
  private ObjectMapper serializer;

  @BeforeEach
  public void setUp() throws IOException {
    serializer = mock(ObjectMapper.class);
    saveDocumentationAsFile =
        new SaveDocumentationAsFile(serializer, Paths.get("target", "candy-doc.json")) {};
    boundedContextsCaptor = ArgumentCaptor.forClass(List.class);

    executeSaveAndCaptureExtractedConcepts();
  }

  private void executeSaveAndCaptureExtractedConcepts() throws IOException {
    List<Event> occurredExtractionEvents =
        List.of(
            BoundedContextFound.builder()
                .simpleName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .description("test package 1")
                .build(),
            CoreConceptFound.builder()
                .simpleName("core concept 1")
                .canonicalName(
                    "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .description("core concept 1")
                .boundedContext("io.candydoc.sample.bounded_context_for_core_concepts_tests")
                .build());

    saveDocumentationAsFile.save(occurredExtractionEvents);

    verify(serializer, times(1)).writeValue(any(File.class), boundedContextsCaptor.capture());
  }

  @Test
  void serialization_get_correct_bounded_context_name() {
    Assertions.assertThat(boundedContextsCaptor.getValue())
        .extracting("name")
        .containsExactlyInAnyOrder("io.candydoc.sample.bounded_context_for_core_concepts_tests");
  }

  @Test
  void serialization_get_correct_bounded_context_description() {
    Assertions.assertThat(boundedContextsCaptor.getValue())
        .extracting("description")
        .containsExactlyInAnyOrder("test package 1");
  }

  @Test
  void serialization_get_correct_core_concept() {
    Assertions.assertThat(boundedContextsCaptor.getValue())
        .extracting("coreConcepts")
        .contains(
            List.of(
                ConceptDto.builder()
                    .simpleName("core concept 1")
                    .canonicalName(
                        "io.candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                    .description("core concept 1")
                    .type(ConceptType.CORE_CONCEPT)
                    .interactsWith(Set.of())
                    .errors(List.of())
                    .build()));
  }
}
