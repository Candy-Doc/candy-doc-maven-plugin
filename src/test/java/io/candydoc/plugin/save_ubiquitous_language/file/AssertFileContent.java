package io.candydoc.plugin.save_ubiquitous_language.file;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.assertj.core.api.AbstractAssert;

public class AssertFileContent extends AbstractAssert<AssertFileContent, Path> {

  protected ObjectMapper trustedMapper;

  protected AssertFileContent(Path actual) {
    super(actual, AssertFileContent.class);
  }

  public static AssertFileContent assertThat(Path actual) {
    return new AssertFileContent(actual);
  }

  public AssertFileContent exists() {
    isNotNull();
    org.assertj.core.api.Assertions.assertThat(actual).exists();
    return this;
  }

  public AssertJsonContent json() {
    return new AssertJsonContent(actual);
  }

  public AssertYmlContent yml() {
    return new AssertYmlContent(actual);
  }

  public AssertFileContent isSameAs(Path reference) {
    isNotNull();
    try {
      JsonNode actualJson = trustedMapper.readTree(actual.toFile());
      JsonNode referenceJson = trustedMapper.readTree(reference.toFile());

      org.assertj.core.api.Assertions.assertThat(actualJson).isEqualTo(referenceJson);
    } catch (IOException e) {
      failWithMessage("Failed to read files <%s> or <%s>", actual, reference);
    }
    return this;
  }

  public static class AssertJsonContent extends AssertFileContent {

    private AssertJsonContent(Path actual) {
      super(actual);
      trustedMapper = new ObjectMapper(new JsonFactory());
    }

    public AssertFileContent isSameAs(Path reference) {
      super.isSameAs(reference);
      return this;
    }
  }

  public static class AssertYmlContent extends AssertFileContent {

    private AssertYmlContent(Path actual) {
      super(actual);
      trustedMapper = new ObjectMapper(new YAMLFactory());
    }

    public AssertFileContent isSameAs(Path reference) {
      super.isSameAs(reference);
      return this;
    }
  }
}
