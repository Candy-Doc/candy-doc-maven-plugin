package io.candydoc.infra;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public class FreemarkerEngine implements TemplateEngine {

    @Override
    public void generatePage(String templateName, Path fileDestination, Map<String, Object> model) {
        try {
            Writer writer = new FileWriter(String.valueOf(fileDestination), StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateName + ".ftlh");
            template.process(model, writer);
            writer.close();
        } catch (IOException | TemplateException e) {
            throw new DocumentationGenerationFailed(e.getMessage());
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
            throw new DocumentationGenerationFailed(e.getMessage());
        }
    }

    @Override
    public String generateFragment(String templateName, Map<String, Object> model) {
        try {
            StringWriter writer = new StringWriter();
            Template template = getFreemarkerTemplate(templateName + ".ftlh");
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new DocumentationGenerationFailed(e.getMessage());
        }
    }

    private Template getFreemarkerTemplate(String freemarkerTemplateFile) throws IOException {
        Configuration freemarkerConfiguration = getFreemarkerConfiguration();
        return freemarkerConfiguration.getTemplate(freemarkerTemplateFile);
    }

    private Configuration getFreemarkerConfiguration() {
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_29);
        freemarkerConfiguration.setClassLoaderForTemplateLoading(this.getClass()
                .getClassLoader(), "web/templates/freemarker");
        return freemarkerConfiguration;
    }
}
