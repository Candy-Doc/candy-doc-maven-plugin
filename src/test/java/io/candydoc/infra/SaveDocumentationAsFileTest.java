package io.candydoc.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.domain.model.BoundedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.*;

class SaveDocumentationAsFileTest {

    SaveDocumentationAsFile saveDocumentationAsFile;
    ObjectMapper serializer;

    @BeforeEach
    public void setUp() {
        serializer = mock(ObjectMapper.class);
        saveDocumentationAsFile = new SaveDocumentationAsFile(serializer, Paths.get("target", "candy-doc.json"));
    }

    @Test
    void delegate_serialization_to_object_mapper() throws IOException {
        saveDocumentationAsFile.save(List.of(BoundedContext.builder()
                .name("candydoc.sample.bounded_context_one")
                .description("test package 1")
                .build()));
        verify(serializer, times(1))
                .writeValue(new File("target/candy-doc.json"), List.of(BoundedContext.builder()
                        .name("candydoc.sample.bounded_context_one")
                        .description("test package 1")
                        .build()));
    }
}