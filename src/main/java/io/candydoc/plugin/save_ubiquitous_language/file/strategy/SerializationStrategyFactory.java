package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.nio.file.Path;

public class SerializationStrategyFactory {
  public static SerializationStrategy forJson(Path destinationFolder) {
    return new DefaultSerializationStrategy(
        new ObjectMapper(new JsonFactory()), destinationFolder, "json");
  }

  public static SerializationStrategy forYml(Path destinationFolder) {
    return new DefaultSerializationStrategy(
        new ObjectMapper(new YAMLFactory()), destinationFolder, "yml");
  }
}
