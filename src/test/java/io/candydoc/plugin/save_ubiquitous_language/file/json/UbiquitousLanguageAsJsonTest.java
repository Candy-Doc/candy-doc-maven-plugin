package io.candydoc.plugin.save_ubiquitous_language.file.json;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Path;

class UbiquitousLanguageAsJsonTest {

    static final Path target = Path.of("target", "candy-doc-test.json");

    UbiquitousLanguageAsJson ubiquitousLanguageAsJson;

    @BeforeEach
    public void setUp() {
        this.ubiquitousLanguageAsJson = ubiquitousLanguageAsJson;
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(target.toFile());
    }

}
