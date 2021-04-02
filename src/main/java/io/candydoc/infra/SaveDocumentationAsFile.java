package io.candydoc.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.domain.model.BoundedContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Value
@RequiredArgsConstructor
class SaveDocumentationAsFile implements SaveDocumentationPort {

    ObjectMapper serializer;
    Path fileToSave;

    @Override
    public void save(List<BoundedContext> boundedContexts) throws IOException {
        Files.createDirectories(fileToSave.getParent());
        serializer.writeValue(fileToSave.toFile(), boundedContexts);
    }
}
