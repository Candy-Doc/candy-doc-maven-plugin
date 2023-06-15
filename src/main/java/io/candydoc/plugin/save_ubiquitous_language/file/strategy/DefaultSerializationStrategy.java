package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageSavingFailed;
import java.io.IOException;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSerializationStrategy implements SerializationStrategy {

  private final ObjectMapper serializer;
  @Getter private final Path destinationFolder;
  @Getter private final String extension;

  //  public SerializationStrategy initDirectory() {
  //    try {
  //      Files.createDirectories(destinationFolder);
  //    } catch (IOException e) {
  //      throw new UbiquitousLanguageSavingFailed(e);
  //    }
  //    return this;
  //  }

  @Override
  public Path writeToFile(String filename, Object dto) {
    String fileName = getFileName(filename);
    try {
      serializer.writeValue(destinationFolder.resolve(fileName).toFile(), dto);
    } catch (IOException e) {
      throw new UbiquitousLanguageSavingFailed(e);
    }
    return Path.of("./", fileName);
  }

  private String getFileName(String name) {
    return name.replace(" ", "_") + "." + extension;
  }
}
