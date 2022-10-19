package io.candydoc.plugin.save_ubiquitous_language.file.yml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.candydoc.plugin.save_ubiquitous_language.file.UbiquitousLanguageAsFile;
import java.nio.file.Path;

public class UbiquitousLanguageAsYml extends UbiquitousLanguageAsFile {
  public UbiquitousLanguageAsYml(Path fileToSave) {
    super(new ObjectMapper(new YAMLFactory()), fileToSave);
  }
}
