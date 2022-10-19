package io.candydoc.plugin.save_ubiquitous_language.file.json;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.fixture.UbiquitousLanguageFixtures;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UbiquitousLanguageAsJsonTest {

    static final Path target = Path.of("target", "candy-doc-test.json");

    UbiquitousLanguageAsJson ubiquitousLanguageAsJson;

    @BeforeEach
    public void setUp() {
        this.ubiquitousLanguageAsJson = new UbiquitousLanguageAsJson(target);
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(target.toFile());
    }

    @Test
    public void save_bounded_context_as_json() throws IOException {
        // given
        Set<DomainModel> domainModels = Stream.of(
                UbiquitousLanguageFixtures.BOUNDED_CONTEXT,
                UbiquitousLanguageFixtures.SHARED_KERNEL
            ).flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());

        Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainModels(domainModels);

        // when
        ubiquitousLanguageAsJson.saveAll(ubiquitousLanguages);

        // then
        DocumentContext actualJson = JsonPath.parse(target.toFile());

        assertThat(actualJson.read("$.boundedContexts[0].packageName").toString())
            .isEqualTo("io.candydoc.ddd.strategic_patterns.bounded_context.sample");
    }

}
