package io.candydoc.plugin.save_documentation.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.extract_domain_models.UbiquitousLanguageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import io.candydoc.ddd.model.UbiquitousLanguage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class UbiquitousLanguageAsFile implements UbiquitousLanguageRepository {

  private final ObjectMapper serializer;
  private final Path fileToSave;

  @Override
  public void saveAll(Set<UbiquitousLanguage> ubiquitousLanguages) {
    try {
      Files.createDirectories(fileToSave.getParent());
      serializer.writeValue(fileToSave.toFile(), null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
