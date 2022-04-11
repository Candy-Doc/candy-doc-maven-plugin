package io.candydoc.infra;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.candydoc.domain.SaveDocumentationAdapterFactory;
import io.candydoc.domain.SaveDocumentationPort;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveDocumentationAdapterFactoryImpl implements SaveDocumentationAdapterFactory {

  @Override
  public SaveDocumentationPort getAdapter(String outputFormat, String outputDirectory) {
    if (Files.notExists((Path.of(outputDirectory)))) {
      if (!Path.of(outputDirectory).toFile().mkdirs())
        throw new IllegalArgumentException("Output directory does not exist and can't be created.");
    }
    Path outputFile = Paths.get(outputDirectory, "bounded_contexts." + outputFormat);
    switch (outputFormat) {
      case "json":
        return new SaveDocumentationAsFile(new ObjectMapper(new JsonFactory()), outputFile);
      case "yml":
        return new SaveDocumentationAsFile(new ObjectMapper(new YAMLFactory()), outputFile);
      case "html":
        return new SaveDocumentationAsHTML(new FreemarkerEngine());
    }
    throw new IllegalArgumentException("File format must be 'json', 'yml' or 'html'.");
  }
}
