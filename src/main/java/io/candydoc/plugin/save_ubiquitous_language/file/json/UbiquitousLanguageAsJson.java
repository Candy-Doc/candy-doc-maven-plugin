package io.candydoc.plugin.save_ubiquitous_language.file.json;

import io.candydoc.plugin.save_ubiquitous_language.file.UbiquitousLanguageAsFile;
import io.candydoc.plugin.save_ubiquitous_language.file.strategy.SerializationStrategyFactory;
import java.nio.file.Path;

public class UbiquitousLanguageAsJson extends UbiquitousLanguageAsFile {
  public UbiquitousLanguageAsJson(Path destinationFolder) {
    super(SerializationStrategyFactory.forJson(destinationFolder).initDirectory());
  }
}
