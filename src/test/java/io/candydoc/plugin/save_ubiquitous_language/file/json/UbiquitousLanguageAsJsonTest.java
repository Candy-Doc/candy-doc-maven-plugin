package io.candydoc.plugin.save_ubiquitous_language.file.json;

import io.candydoc.ddd.model.FoundDomainObjects;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.fixture.DomainVocabularyFixtures;
import io.candydoc.plugin.save_ubiquitous_language.file.AssertFileContent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

class UbiquitousLanguageAsJsonTest {

    static final Path REFERENCE_FOLDER = Path.of("src", "test", "resources", "json");
    static final Path DESTINATION_FOLDER = Path.of("target", "candyDocTmp");

    UbiquitousLanguageAsJson ubiquitousLanguageAsJson;

    @BeforeEach
    public void setUp() throws IOException {
        this.ubiquitousLanguageAsJson = new UbiquitousLanguageAsJson(DESTINATION_FOLDER);
    }

    @AfterEach
    public void tearDown() {
        DESTINATION_FOLDER.toFile().deleteOnExit();
    }

    @Test
    public void save_bounded_context_as_json() {
        // given
        FoundDomainObjects foundDomainObjects = new FoundDomainObjects()
                .addAll(DomainVocabularyFixtures.DOMAIN_OBJECTS_IN_BOUNDED_CONTEXT);

        Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

        // when
        ubiquitousLanguageAsJson.saveAll(ubiquitousLanguages);

        // then
        Path referenceFile = REFERENCE_FOLDER.resolve("bounded_context_one.json");
        Path actualFile = DESTINATION_FOLDER.resolve("bounded_context_one.json");

        AssertFileContent.assertThatJson(actualFile)
                .isEqualTo(referenceFile);
    }

    @Test
    public void save_shared_kernel_as_json() {
        // given
        FoundDomainObjects foundDomainObjects = new FoundDomainObjects()
                .addAll(DomainVocabularyFixtures.DOMAIN_OBJECTS_IN_SHARED_KERNEL);

        Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

        // when
        ubiquitousLanguageAsJson.saveAll(ubiquitousLanguages);

        // then
        Path referenceFile = REFERENCE_FOLDER.resolve("shared_kernel_one.json");
        Path actualFile = DESTINATION_FOLDER.resolve("shared_kernel_one.json");

        AssertFileContent.assertThatJson(actualFile)
                .isEqualTo(referenceFile);
    }

    @Test
    public void save_orphan_vocabulary_as_json() {
        // given
        FoundDomainObjects foundDomainObjects = new FoundDomainObjects()
                .addAll(DomainVocabularyFixtures.ORPHAN_DOMAIN_OBJECTS);

        Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

        // when
        ubiquitousLanguageAsJson.saveAll(ubiquitousLanguages);

        // then
        Path referenceFile = REFERENCE_FOLDER.resolve("orphan_vocabulary.json");
        Path actualFile = DESTINATION_FOLDER.resolve("orphan_vocabulary.json");

        AssertFileContent.assertThatJson(actualFile)
                .isEqualTo(referenceFile);
    }

}
