package io.candydoc.plugin.save_ubiquitous_language.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.candydoc.plugin.save_ubiquitous_language.file.json.UbiquitousLanguageAsJson;
import io.candydoc.plugin.save_ubiquitous_language.file.yml.UbiquitousLanguageAsYml;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class UbiquitousLanguageAsFileTest {

  @ParameterizedTest
  @MethodSource("validFormat")
  void valid_output_format(String validFormat, Class<?> expectedInstance) {
    UbiquitousLanguageAsFile ubiquitousLanguageAsFile =
        UbiquitousLanguageAsFile.forFormat(validFormat, "target");

    assertThat(ubiquitousLanguageAsFile).isInstanceOf(expectedInstance);
    assertThat(ubiquitousLanguageAsFile.getExtension()).isEqualTo(validFormat);
    assertThat(ubiquitousLanguageAsFile.getDestinationFolder())
        .isEqualTo(Path.of("target", "candy-doc", "ubiquitous_language"));
  }

  public static Stream<Arguments> validFormat() {
    return Stream.of(
        Arguments.of("json", UbiquitousLanguageAsJson.class),
        Arguments.of("yml", UbiquitousLanguageAsYml.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"txt", "adoc"})
  void invalid_output_format(String invalidFormat) {
    ThrowableAssert.ThrowingCallable callable =
        () -> UbiquitousLanguageAsFile.forFormat(invalidFormat, "target");

    assertThatThrownBy(callable)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Supported file formats are 'json', 'yml'.");
  }

  @Test
  void output_directory_must_exist() {
    ThrowableAssert.ThrowingCallable callable =
        () -> UbiquitousLanguageAsFile.forFormat("json", "dir_that_does_not_exist");

    assertThatThrownBy(callable)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Output directory 'dir_that_does_not_exist' does not exist.");
  }
}
