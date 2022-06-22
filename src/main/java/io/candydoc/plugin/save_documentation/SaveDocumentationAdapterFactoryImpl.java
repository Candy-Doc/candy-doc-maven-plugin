package io.candydoc.plugin.save_documentation;

import io.candydoc.ddd.extract_ddd_concepts.SaveDocumentationPort;
import io.candydoc.plugin.save_documentation.file.json.SaveDocumentationAsJson;
import io.candydoc.plugin.save_documentation.file.yml.SaveDocumentationAsYml;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveDocumentationAdapterFactoryImpl implements SaveDocumentationAdapterFactory {

  @Override
  public SaveDocumentationPort getAdapter(String outputFormat, String outputDirectory) {
    if (Files.notExists((Path.of(outputDirectory)))) {
      throw new IllegalArgumentException("Output directory does not exist.");
    }
    Path outputFile = Paths.get(outputDirectory, "candy-doc", "bounded_contexts." + outputFormat);
    switch (outputFormat) {
      case "json":
        return new SaveDocumentationAsJson(outputFile);
      case "yml":
        return new SaveDocumentationAsYml(outputFile);
    }
    throw new IllegalArgumentException("File format must be 'json' or 'yml'.");
  }
}
