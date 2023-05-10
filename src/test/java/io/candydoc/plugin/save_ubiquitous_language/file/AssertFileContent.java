package io.candydoc.plugin.save_ubiquitous_language.file;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.assertj.core.api.AbstractAssert;

public class AssertFileContent extends AbstractAssert<AssertFileContent, Path> {

  private ObjectMapper trustedMapper;

  protected AssertFileContent(Path actual) {
    super(actual, AssertFileContent.class);
  }

  public static AssertFileContent assertThat(Path actual) {
    return new AssertFileContent(actual);
  }

  public AssertFileContent exists() {
    isNotNull();
    if (!actual.toFile().exists()) {
      failWithMessage("File %s doesn't exists", actual);
    }
    return this;
  }

  public AssertFileContent json() {
    isNotNull();
    trustedMapper = new ObjectMapper(new JsonFactory());
    return this;
  }

  public AssertFileContent yml() {
    isNotNull();
    trustedMapper = new ObjectMapper(new YAMLFactory());
    return this;
  }

  public AssertFileContent isSameHas(Path reference) {
    isNotNull();
    if (trustedMapper == null) {
      failWithMessage("You have to call json() or yml() first");
    }
    try {
      JsonNode actualJson = trustedMapper.readTree(actual.toFile());
      JsonNode referenceJson = trustedMapper.readTree(reference.toFile());

      org.assertj.core.api.Assertions.assertThat(actualJson).isEqualTo(referenceJson);
    } catch (IOException e) {
      fail(e);
    }
    return this;
  }
}
