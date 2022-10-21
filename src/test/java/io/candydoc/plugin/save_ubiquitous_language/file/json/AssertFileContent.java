package io.candydoc.plugin.save_ubiquitous_language.file.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertFileContent {

    private final Path actualPath;

    public AssertFileContent isEqualTo(Path expectedPath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode actualJson = mapper.readTree(actualPath.toFile());
            JsonNode expectedJson = mapper.readTree(expectedPath.toFile());

            assertThat(actualJson).isEqualTo(expectedJson);
        } catch (Exception e) {
            fail(e);
        }

        return this;
    }

    public static AssertFileContent assertThatJson(Path actualPath) {
        return new AssertFileContent(actualPath);
    }
}
