package io.candydoc.plugin.save_documentation.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_domain_models.SaveUbiquitousLanguagePort;
import io.candydoc.plugin.model.BoundedContextDtoMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SaveUbiquitousLanguageAsFile implements SaveUbiquitousLanguagePort {

  private final ObjectMapper serializer;
  private final Path fileToSave;

  @Override
  public void save(List<Event> domainEvents) throws IOException {
    Files.createDirectories(fileToSave.getParent());
    serializer.writeValue(fileToSave.toFile(), BoundedContextDtoMapper.map(domainEvents));
  }
}
