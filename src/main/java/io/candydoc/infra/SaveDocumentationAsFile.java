package io.candydoc.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.domain.events.DomainEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SaveDocumentationAsFile implements SaveDocumentationPort {

  private final ObjectMapper serializer;
  private final Path fileToSave;

  @Override
  public void save(List<DomainEvent> domainEvents) throws IOException {
    Files.createDirectories(fileToSave.getParent());
    serializer.writeValue(fileToSave.toFile(), BoundedContextDtoMapper.map(domainEvents));
  }
}
