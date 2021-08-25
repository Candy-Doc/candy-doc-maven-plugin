package io.candydoc.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.domain.events.BoundedContextFound;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.infra.model.BoundedContextDto;
import io.candydoc.infra.model.ConceptDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class SaveDocumentationAsFileTest {

    SaveDocumentationAsFile saveDocumentationAsFile;
    ArgumentCaptor<List> resultCaptor;
    ObjectMapper serializer;

    @BeforeEach
    public void setUp() throws IOException {
        serializer = mock(ObjectMapper.class);
        saveDocumentationAsFile = new SaveDocumentationAsFile(serializer, Paths.get("target", "candy-doc.json"));
        resultCaptor = initDocumentationGenerationTests();
    }

    ArgumentCaptor<List> initDocumentationGenerationTests() throws IOException {
        saveDocumentationAsFile.save(List.of(BoundedContextFound.builder()
                .name("candydoc.sample.bounded_context_for_core_concepts_tests")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .description("test package 1")
                .build(), CoreConceptFound.builder()
                .name("core concept 1")
                .className("CoreConcept1")
                .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                .packageName("candydoc.sample.bounded_context_for_core_concepts_tests")
                .description("core concept 1")
                .boundedContext("candydoc.sample.bounded_context_for_core_concepts_tests")
                .build()));
        ArgumentCaptor<List> resultCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<File> test = ArgumentCaptor.forClass(File.class);
        verify(serializer, times(1)).writeValue(test.capture(), resultCaptor.capture());
        return resultCaptor;
    }

    @Test
    void serialization_get_correct_bounded_context_name() {
        Assertions.assertThat(resultCaptor.getValue()).extracting("name")
                .containsExactlyInAnyOrder("candydoc.sample.bounded_context_for_core_concepts_tests");
    }

    @Test
    void serialization_get_correct_bounded_context_description() {
        Assertions.assertThat(resultCaptor.getValue()).extracting("description")
                .containsExactlyInAnyOrder("test package 1");
    }

    @Test
    void serialization_get_correct_core_concept() {
        Assertions.assertThat(resultCaptor.getValue()).extracting("coreConcepts")
                .contains(List.of(ConceptDto.builder()
                        .name("core concept 1")
                        .fullName("candydoc.sample.bounded_context_for_core_concepts_tests.CoreConcept1")
                        .description("core concept 1")
                        .conceptType(BoundedContextDto.ConceptType.CORE_CONCEPT)
                        .interactsWith(Set.of())
                        .errors(List.of())
                        .build()));
    }
}