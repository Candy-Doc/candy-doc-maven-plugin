package io.candydoc.plugin.save_ubiquitous_language.file;

import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageRepository;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.model.ManifestDto;
import io.candydoc.plugin.model.UbiquitousLanguageDto;
import io.candydoc.plugin.save_ubiquitous_language.file.json.UbiquitousLanguageAsJson;
import io.candydoc.plugin.save_ubiquitous_language.file.strategy.SerializationStrategy;
import io.candydoc.plugin.save_ubiquitous_language.file.yml.UbiquitousLanguageAsYml;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public abstract class UbiquitousLanguageAsFile implements UbiquitousLanguageRepository {

  private final SerializationStrategy serializationStrategy;

  @Override
  public void saveAll(Set<UbiquitousLanguage> ubiquitousLanguages) {
    List<String> filePaths =
        ubiquitousLanguages.stream()
            .map(ubiquitousLanguage -> saveToFile(ubiquitousLanguage).toString())
            .collect(Collectors.toList());

    Manifest.generateFrom(this.serializationStrategy, filePaths);
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

  public String getExtension() {
    return this.serializationStrategy.getExtension();
  }

  public Path getDestinationFolder() {
    return this.serializationStrategy.getDestinationFolder();
  }

  private Path saveToFile(UbiquitousLanguage ubiquitousLanguage) {
    UbiquitousLanguageDto dto = UbiquitousLanguageDto.from(ubiquitousLanguage);
    return serializationStrategy.writeToFile(dto.getName().toLowerCase(), dto);
  }

  private static class Manifest {
    public static void generateFrom(
        SerializationStrategy serializationStrategy, List<String> filePaths) {
      ManifestDto manifestDto = ManifestDto.from(filePaths);
      serializationStrategy.writeToFile("MANIFEST", manifestDto);
    }
  }
}
