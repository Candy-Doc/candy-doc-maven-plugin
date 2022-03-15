package io.candydoc.infra;

import java.nio.file.Path;
import java.util.Map;

public interface TemplateEngine {
  void generatePage(String templateName, Path pageName, Map<String, Object> model);

  void generateStyle(Path pageName);
}
