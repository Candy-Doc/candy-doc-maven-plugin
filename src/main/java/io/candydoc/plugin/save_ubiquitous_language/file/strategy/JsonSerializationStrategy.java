package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;

public class JsonSerializationStrategy extends SerializationStrategy {
  public JsonSerializationStrategy(Path destinationFolder) {
    super(new ObjectMapper(new JsonFactory()), destinationFolder, "json");
  }
}
