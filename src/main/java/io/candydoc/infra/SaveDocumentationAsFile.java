package io.candydoc.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@RequiredArgsConstructor
class SaveDocumentationAsFile implements SaveDocumentationPort {

    private final ObjectMapper serializer;
    private final Path fileToSave;

    @Override
    public void save(List<DomainEvent> domainEvents) throws IOException {
        BoundedContextDtoMapper boundedContextDtoMapper = new BoundedContextDtoMapper();
        Files.createDirectories(fileToSave.getParent());
        serializer.writeValue(fileToSave.toFile(), boundedContextDtoMapper.map(domainEvents));
    }
}
