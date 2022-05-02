package io.candydoc.plugin.save_documentation.html;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.candydoc.ddd.extract_ddd_concepts.PluginArgumentsException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FreemarkerEngine implements TemplateEngine {

  ResourceBundle bundle = ResourceBundle.getBundle("ConceptsBase");

  private Map<String, Object> modelWithI18n(Map<String, Object> model) {
    Map<String, Object> modelWithI18n = new HashMap<>(model);
    modelWithI18n.put("i18n", bundle);
    return modelWithI18n;
  }

  @Override
  public void generatePage(String templateName, Path fileDestination, Map<String, Object> model) {
    try {
      Writer writer = new FileWriter(String.valueOf(fileDestination), StandardCharsets.UTF_8);
      Template template = getFreemarkerTemplate(templateName + ".ftlh");
      template.process(modelWithI18n(model), writer);
      writer.close();
    } catch (IOException | TemplateException e) {
      throw new PluginArgumentsException(e.getMessage());
    }
  }

  @Override
  public void generateStyle(Path fileDestination) {
    try {
      Writer writer = new FileWriter(String.valueOf(fileDestination), StandardCharsets.UTF_8);
      Template template = getFreemarkerTemplate("style.css");
      template.process(null, writer);
      writer.close();
    } catch (IOException | TemplateException e) {
      throw new PluginArgumentsException(e.getMessage());
    }
  }

  private Template getFreemarkerTemplate(String freemarkerTemplateFile) throws IOException {
    Configuration freemarkerConfiguration = getFreemarkerConfiguration();
    return freemarkerConfiguration.getTemplate(freemarkerTemplateFile);
  }

  private Configuration getFreemarkerConfiguration() {
    Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_29);
    freemarkerConfiguration.setClassLoaderForTemplateLoading(
        this.getClass().getClassLoader(), "web/templates/freemarker");
    return freemarkerConfiguration;
  }
}
