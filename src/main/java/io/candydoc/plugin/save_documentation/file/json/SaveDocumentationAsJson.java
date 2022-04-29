package io.candydoc.plugin.save_documentation.file.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.candydoc.plugin.save_documentation.file.SaveDocumentationAsFile;
import java.nio.file.Path;

public class SaveDocumentationAsJson extends SaveDocumentationAsFile {
  public SaveDocumentationAsJson(Path fileToSave) {
    super(new ObjectMapper(new JsonFactory()), fileToSave);
  }
}
