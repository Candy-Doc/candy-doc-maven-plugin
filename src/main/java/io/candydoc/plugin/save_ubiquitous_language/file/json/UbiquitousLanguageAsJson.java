package io.candydoc.plugin.save_ubiquitous_language.file.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.plugin.save_ubiquitous_language.file.UbiquitousLanguageAsFile;
import java.nio.file.Path;

public class UbiquitousLanguageAsJson extends UbiquitousLanguageAsFile {
  public UbiquitousLanguageAsJson(Path destinationFolder) {
    super(new ObjectMapper(new JsonFactory()), destinationFolder, "json");
  }
}
