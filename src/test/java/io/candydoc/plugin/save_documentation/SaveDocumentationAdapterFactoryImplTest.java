package io.candydoc.plugin.save_documentation;

import io.candydoc.ddd.bounded_context.BoundedContextFound;
import io.candydoc.ddd.extract_ddd_concepts.SaveDocumentationPort;
import io.candydoc.plugin.save_documentation.file.SaveDocumentationAsFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SaveDocumentationAdapterFactoryImplTest {

  private final SaveDocumentationAdapterFactory adapterFactory =
      new SaveDocumentationAdapterFactoryImpl();

  @Test
  void throws_an_exception_when_output_format_is_wrong() {
    String outputFormat = "wrongFormat";
    String outputDirectory = "target";
    Assertions.assertThatThrownBy(() -> adapterFactory.getAdapter(outputFormat, outputDirectory))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("File format must be 'json', 'yml' or 'html'.");
  }

  @Test
  void throws_an_exception_when_output_directory_is_wrong() {
    String outputFormat = "json";
    String outputDirectory = "wrongOutputDirectory";
    Assertions.assertThatThrownBy(() -> adapterFactory.getAdapter(outputFormat, outputDirectory))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Output directory does not exist.");
  }

  @Test
  void save_documentation_as_json() throws IOException {
    SaveDocumentationPort saveDocumentationPort = adapterFactory.getAdapter("json", "target");
    BoundedContextFound newBoundedContextEvent =
        BoundedContextFound.builder()
            .simpleName("io.candydoc.sample.bounded_context_one")
            .packageName("io.candydoc.sample.bounded_context_one")
            .description("test package 1")
            .build();
    saveDocumentationPort.save(new LinkedList<>(Collections.singleton(newBoundedContextEvent)));
    Path expectedPath = Paths.get("target", "candy-doc", "bounded_contexts.json");
    Assertions.assertThat(saveDocumentationPort).isInstanceOf(SaveDocumentationAsFile.class);
    Assertions.assertThat(expectedPath).exists();
    Files.delete(expectedPath);
  }

  @Test
  void save_documentation_as_yml() throws IOException {
    SaveDocumentationPort saveDocumentationPort = adapterFactory.getAdapter("yml", "target");
    BoundedContextFound newBoundedContextEvent =
        BoundedContextFound.builder()
            .simpleName("io.candydoc.sample.bounded_context_one")
            .packageName("io.candydoc.sample.bounded_context_one")
            .description("test package 1")
            .build();
    saveDocumentationPort.save(new LinkedList<>(Collections.singleton(newBoundedContextEvent)));
    Path expectedPath = Paths.get("target", "candy-doc", "bounded_contexts.yml");
    Assertions.assertThat(saveDocumentationPort).isInstanceOf(SaveDocumentationAsFile.class);
    Assertions.assertThat(expectedPath).exists();
    Files.delete(expectedPath);
  }
}
