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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
      List<String> filePaths =
          ubiquitousLanguages.stream()
              .map(ubiquitousLanguage -> saveToFile(ubiquitousLanguage).toString())
              .collect(Collectors.toList());

      Manifest.generateFrom(this, filePaths);
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

  private Path saveToFile(UbiquitousLanguage ubiquitousLanguage) {
    UbiquitousLanguageDto dto = UbiquitousLanguageDto.from(ubiquitousLanguage);
    String fileName = getFileName(dto.getName());
    try {
      serializer.writeValue(destinationFolder.resolve(fileName).toFile(), dto);
    } catch (IOException e) {
      throw new UbiquitousLanguageSavingFailed(e);
    }
    return Path.of("./", fileName);
  }

  private String getFileName(String name) {
    return name.toLowerCase().replace(" ", "_") + "." + extension;
  }

  private static class Manifest {
    public static void generateFrom(
        UbiquitousLanguageAsFile ubiquitousLanguageAsFile, List<String> filePaths)
        throws IOException {
      ManifestDto manifestDto = ManifestDto.builder().files(filePaths).build();

      Files.createDirectories(ubiquitousLanguageAsFile.destinationFolder);
      ubiquitousLanguageAsFile.serializer.writeValue(
          ubiquitousLanguageAsFile
              .destinationFolder
              .resolve("MANIFEST." + ubiquitousLanguageAsFile.extension)
              .toFile(),
          manifestDto);
    }
  }
}
