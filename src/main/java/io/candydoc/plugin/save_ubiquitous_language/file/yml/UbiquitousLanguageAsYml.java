package io.candydoc.plugin.save_ubiquitous_language.file.yml;

import io.candydoc.plugin.save_ubiquitous_language.file.UbiquitousLanguageAsFile;
import io.candydoc.plugin.save_ubiquitous_language.file.strategy.SerializationStrategyFactory;
import java.nio.file.Path;

public class UbiquitousLanguageAsYml extends UbiquitousLanguageAsFile {
  public UbiquitousLanguageAsYml(Path destinationFolder) {
    super(SerializationStrategyFactory.forYml(destinationFolder));
  }
}
