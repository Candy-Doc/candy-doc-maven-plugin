package io.candydoc.plugin.save_ubiquitous_language.file.json;

import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.plugin.fixture.UbiquitousLanguageFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

class UbiquitousLanguageAsJsonTest {

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
    public void save_bounded_context_as_json() throws IOException {
        // given
        Set<DomainModel> domainModels = Stream.of(
                        UbiquitousLanguageFixtures.BOUNDED_CONTEXT,
                        UbiquitousLanguageFixtures.SHARED_KERNEL)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());

        Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainModels(domainModels);

        // when
        ubiquitousLanguageAsJson.saveAll(ubiquitousLanguages);

        // then
        Path boundedContextFilePath = DESTINATION_FOLDER.resolve("bounded_context_one.json");

        JsonAssert.with(Files.newInputStream(boundedContextFilePath))
                .assertThat("$.contextName", equalTo("Bounded context one"))
                .assertThat("$.description", equalTo("Describe the bounded context here"))
                .assertThat("$.boundedContext", equalTo(true));
    }

}
