package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.nio.file.Path;

public class YmlSerializationStrategy extends SerializationStrategy {
  public YmlSerializationStrategy(Path destinationFolder) {
    super(new ObjectMapper(new YAMLFactory()), destinationFolder, "yml");
  }
}
