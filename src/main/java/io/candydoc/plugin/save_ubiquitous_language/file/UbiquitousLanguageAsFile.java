package io.candydoc.plugin.save_ubiquitous_language.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageRepository;
import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageSavingFailed;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.model.ManifestDto;
import io.candydoc.plugin.model.UbiquitousLanguageDto;
import io.candydoc.plugin.save_ubiquitous_language.file.json.UbiquitousLanguageAsJson;
import io.candydoc.plugin.save_ubiquitous_language.file.yml.UbiquitousLanguageAsYml;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public abstract class UbiquitousLanguageAsFile implements UbiquitousLanguageRepository {

  private final ObjectMapper serializer;
  @Getter private final Path destinationFolder;
  @Getter private final String extension;

  @Override
  public void saveAll(Set<UbiquitousLanguage> ubiquitousLanguages) {
    try {
      Files.createDirectories(destinationFolder);
      Manifest manifest = new Manifest();

      for (UbiquitousLanguage ubiquitousLanguage : ubiquitousLanguages) {
        manifest.addFile(writeToFile(ubiquitousLanguage).toString());
      }

      manifest.save();
    } catch (IOException e) {
      throw new UbiquitousLanguageSavingFailed(e);
    }
  }

  public static UbiquitousLanguageAsFile forFormat(String outputFormat, String outputDir) {
    Path outputDirectory = Path.of(outputDir);

    if (Files.notExists(outputDirectory)) {
      throw new IllegalArgumentException(
          String.format("Output directory '%s' does not exist.", outputDir));
    }

    Path outputFile = outputDirectory.resolve("candy-doc").resolve("ubiquitous_language");

    switch (outputFormat) {
      case "json":
        return new UbiquitousLanguageAsJson(outputFile);
      case "yml":
        return new UbiquitousLanguageAsYml(outputFile);
    }
    throw new IllegalArgumentException("Supported file formats are 'json', 'yml'.");
  }

  private Path writeToFile(UbiquitousLanguage ubiquitousLanguage) throws IOException {
    UbiquitousLanguageDto dto = UbiquitousLanguageDto.from(ubiquitousLanguage);
    String fileName = getFileName(dto.getName());
    serializer.writeValue(destinationFolder.resolve(fileName).toFile(), dto);
    return Path.of("./", fileName);
  }

  private String getFileName(String name) {
    return name.toLowerCase().replace(" ", "_") + "." + extension;
  }

  private class Manifest {
    private final ManifestDto manifestDto;

    public Manifest() {
      manifestDto = ManifestDto.builder().build();
    }

    public void addFile(String filePath) {
      manifestDto.add(filePath);
    }

    public void save() throws IOException {
      Files.createDirectories(destinationFolder);
      serializer.writeValue(
          destinationFolder.resolve("MANIFEST." + extension).toFile(), manifestDto);
    }
  }
}
