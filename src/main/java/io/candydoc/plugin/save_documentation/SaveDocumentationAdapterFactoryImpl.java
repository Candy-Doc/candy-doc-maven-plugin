package io.candydoc.plugin.save_documentation;

import io.candydoc.ddd.extract_domain_models.SaveUbiquitousLanguagePort;
import io.candydoc.plugin.save_documentation.file.json.SaveUbiquitousLanguageAsJson;
import io.candydoc.plugin.save_documentation.file.yml.SaveUbiquitousLanguageAsYml;
import io.candydoc.plugin.save_documentation.html.FreemarkerEngine;
import io.candydoc.plugin.save_documentation.html.SaveUbiquitousLanguageAsHTML;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveDocumentationAdapterFactoryImpl implements SaveDocumentationAdapterFactory {

  @Override
  public SaveUbiquitousLanguagePort getAdapter(String outputFormat, String outputDirectory) {
    if (Files.notExists((Path.of(outputDirectory)))) {
      throw new IllegalArgumentException("Output directory does not exist.");
    }
    Path outputFile = Paths.get(outputDirectory, "candy-doc", "bounded_contexts." + outputFormat);
    switch (outputFormat) {
      case "json":
        return new SaveUbiquitousLanguageAsJson(outputFile);
      case "yml":
        return new SaveUbiquitousLanguageAsYml(outputFile);
      case "html":
        return new SaveUbiquitousLanguageAsHTML(new FreemarkerEngine());
    }
    throw new IllegalArgumentException("File format must be 'json', 'yml' or 'html'.");
  }
}
