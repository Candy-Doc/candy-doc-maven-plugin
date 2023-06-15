package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import io.candydoc.ddd.extract_domain_vocabulary.UbiquitousLanguageSavingFailed;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface SerializationStrategy {
  Path writeToFile(String filename, Object dto);

  Path getDestinationFolder();

  String getExtension();

  default SerializationStrategy initDirectory() {
    try {
      Files.createDirectories(getDestinationFolder());
    } catch (IOException e) {
      throw new UbiquitousLanguageSavingFailed(e);
    }
    return this;
  }
}
