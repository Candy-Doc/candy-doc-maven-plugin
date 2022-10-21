package io.candydoc.plugin.save_ubiquitous_language.file;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertFileContent {

    private final Path actualPath;
    private final ObjectMapper trustedMapper;

    public AssertFileContent isEqualTo(Path expectedPath) {
        try {
            JsonNode actualJson = trustedMapper.readTree(actualPath.toFile());
            JsonNode expectedJson = trustedMapper.readTree(expectedPath.toFile());

            assertThat(actualJson).isEqualTo(expectedJson);
        } catch (Exception e) {
            fail(e);
        }

        return this;
    }

    public static AssertFileContent assertThatJson(Path actualPath) {
        return new AssertFileContent(actualPath, new ObjectMapper(new JsonFactory()));
    }

    public static AssertFileContent assertThatYml(Path actualPath) {
        return new AssertFileContent(actualPath, new ObjectMapper(new YAMLFactory()));
    }
}
