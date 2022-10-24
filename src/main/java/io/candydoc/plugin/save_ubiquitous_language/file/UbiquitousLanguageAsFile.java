package io.candydoc.plugin.save_ubiquitous_language.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageRepository;
import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageSavingFailed;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.model.UbiquitousLanguageDto;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public abstract class UbiquitousLanguageAsFile implements UbiquitousLanguageRepository {

  private final ObjectMapper serializer;
  private final Path destinationFolder;
  private final String extension;

  @Override
  public void saveAll(Set<UbiquitousLanguage> ubiquitousLanguages) {
    try {
      Files.createDirectories(destinationFolder);

      for (UbiquitousLanguage ubiquitousLanguage : ubiquitousLanguages) {
        UbiquitousLanguageDto dto = UbiquitousLanguageDto.from(ubiquitousLanguage);
        String fileName = dto.getName().toLowerCase().replace(" ", "_");
        serializer.writeValue(destinationFolder.resolve(fileName + "." + extension).toFile(), dto);
      }

    } catch (IOException e) {
      throw new UbiquitousLanguageSavingFailed(e);
    }
  }

}
