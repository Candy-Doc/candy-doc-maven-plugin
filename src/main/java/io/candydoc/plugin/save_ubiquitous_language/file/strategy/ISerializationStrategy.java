package io.candydoc.plugin.save_ubiquitous_language.file.strategy;

import java.nio.file.Path;

public interface ISerializationStrategy {
  Path writeToFile(String filename, Object dto);
}
