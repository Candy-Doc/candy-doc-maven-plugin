package io.candydoc.plugin.save_ubiquitous_language.file.yml;

import static io.candydoc.plugin.save_ubiquitous_language.file.AssertFileContent.assertThatYml;

import io.candydoc.ddd.model.FoundDomainObjects;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.fixture.DomainVocabularyFixtures;
import io.candydoc.plugin.save_ubiquitous_language.file.AssertFileContent;
import io.candydoc.plugin.save_ubiquitous_language.file.Manifest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UbiquitousLanguageAsYmlTest {

  static final Path REFERENCE_FOLDER = Path.of("src", "test", "resources", "yml");
  static final Path DESTINATION_FOLDER = Path.of("target", "candyDocTmp");

  UbiquitousLanguageAsYml ubiquitousLanguageAsYml;

  @BeforeEach
  public void setUp() throws IOException {
    this.ubiquitousLanguageAsYml = new UbiquitousLanguageAsYml(DESTINATION_FOLDER);
  }

  @AfterEach
  public void tearDown() {
    DESTINATION_FOLDER.toFile().deleteOnExit();
  }

  @Test
  public void save_bounded_context_as_yml() {
    // given
    FoundDomainObjects foundDomainObjects =
        new FoundDomainObjects().addAll(DomainVocabularyFixtures.DOMAIN_OBJECTS_IN_BOUNDED_CONTEXT);

    Set<UbiquitousLanguage> ubiquitousLanguages =
        UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

    // when
    ubiquitousLanguageAsYml.saveAll(ubiquitousLanguages);

    // then
    Path referenceFile = REFERENCE_FOLDER.resolve("bounded_context_one.yml");
    Path actualFile = DESTINATION_FOLDER.resolve("bounded_context_one.yml");

    assertThatYml(actualFile).isEqualTo(referenceFile);

    Path manifestReferenceFile = REFERENCE_FOLDER.resolve("MANIFEST_bounded_context_one.yml");
    Path manifestActualFile = Manifest.DESTINATION_FOLDER.resolve("MANIFEST.yml");

    AssertFileContent.assertThatYml(manifestActualFile).isEqualTo(manifestReferenceFile);
  }

  @Test
  public void save_shared_kernel_as_yml() {
    // given
    FoundDomainObjects foundDomainObjects =
        new FoundDomainObjects().addAll(DomainVocabularyFixtures.DOMAIN_OBJECTS_IN_SHARED_KERNEL);

    Set<UbiquitousLanguage> ubiquitousLanguages =
        UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

    // when
    ubiquitousLanguageAsYml.saveAll(ubiquitousLanguages);

    // then
    Path referenceFile = REFERENCE_FOLDER.resolve("shared_kernel_one.yml");
    Path actualFile = DESTINATION_FOLDER.resolve("shared_kernel_one.yml");

    assertThatYml(actualFile).isEqualTo(referenceFile);

    Path manifestReferenceFile = REFERENCE_FOLDER.resolve("MANIFEST_shared_kernel_one.yml");
    Path manifestActualFile = Manifest.DESTINATION_FOLDER.resolve("MANIFEST.yml");

    AssertFileContent.assertThatYml(manifestActualFile).isEqualTo(manifestReferenceFile);
  }

  @Test
  public void save_orphan_vocabulary_as_yml() {
    // given
    FoundDomainObjects foundDomainObjects =
        new FoundDomainObjects().addAll(DomainVocabularyFixtures.ORPHAN_DOMAIN_OBJECTS);

    Set<UbiquitousLanguage> ubiquitousLanguages =
        UbiquitousLanguage.fromDomainObjects(foundDomainObjects);

    // when
    ubiquitousLanguageAsYml.saveAll(ubiquitousLanguages);

    // then
    Path referenceFile = REFERENCE_FOLDER.resolve("orphan_vocabulary.yml");
    Path actualFile = DESTINATION_FOLDER.resolve("orphan_vocabulary.yml");

    assertThatYml(actualFile).isEqualTo(referenceFile);

    Path manifestReferenceFile = REFERENCE_FOLDER.resolve("MANIFEST_orphan_vocabulary.yml");
    Path manifestActualFile = Manifest.DESTINATION_FOLDER.resolve("MANIFEST.yml");

    AssertFileContent.assertThatYml(manifestActualFile).isEqualTo(manifestReferenceFile);
  }
}
