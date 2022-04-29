package io.candydoc.plugin.save_documentation.file.yml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.candydoc.plugin.save_documentation.file.SaveDocumentationAsFile;
import java.nio.file.Path;

public class SaveDocumentationAsYml extends SaveDocumentationAsFile {
  public SaveDocumentationAsYml(Path fileToSave) {
    super(new ObjectMapper(new YAMLFactory()), fileToSave);
  }
}
