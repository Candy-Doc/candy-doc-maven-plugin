package io.candydoc.infra;

import io.candydoc.domain.events.*;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class SaveDocumentationAsHTMLTest {

    private final SaveDocumentationAsHTML saveDocumentationAsHTML = new SaveDocumentationAsHTML(new FreemarkerEngine());
    private final List<DomainEvent> boundedContextsForHTMLGenerationTests = List.of(BoundedContextFound.builder()
            .name("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .description("description of bounded context 1")
            .build(),
        BoundedContextFound.builder()
            .name("candydoc.sample.valid_bounded_contexts.bounded_context_two")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_two")
            .description("description of bounded context 2")
            .build(),
        CoreConceptFound.builder()
            .name("Core Concept 1")
            .description("Description of core concept 1")
            .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build(),
        CoreConceptFound.builder()
            .name("Core Concept 2")
            .description("Description of core concept 2")
            .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build(),
        ValueObjectFound.builder()
            .description("Description of value object 1")
            .name("ValueObject1")
            .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build(),
        DomainEventFound.builder()
            .description("Description of domain event 1")
            .name("DomainEvent1")
            .className("candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1")
            .packageName("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .boundedContext("candydoc.sample.valid_bounded_contexts.bounded_context_one")
            .build(),
        InteractionBetweenConceptFound.builder()
            .from("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
            .with("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept2")
            .build(),
        InteractionBetweenConceptFound.builder()
            .from("candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1")
            .with("candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1")
            .build());

    private final List<DomainEvent> emptyBoundedContext = List.of(BoundedContextFound.builder()
        .name("io.emptyBoundedContext")
        .description("description of empty bounded context")
        .packageName("io.emptyBoundedContext")
        .build());

    @AfterEach
    public void afterEach() throws IOException {
        Path HTMLDestinationFolder = Paths.get("target", "candy-doc", "html");
        FileUtils.deleteDirectory(HTMLDestinationFolder.toFile());
    }

    private Document getDocument(String documentPathInHTMLDirectory) throws IOException {
        File generatedIndexFile = new File(Paths.get("target", "candy-doc", "html", documentPathInHTMLDirectory).toString());
        return Jsoup.parse(generatedIndexFile, "UTF-8", "");
    }

    @Test
    void index_file_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html", "index.html"))))
            .exists();
    }

    @Test
    void css_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html", "style.css"))))
            .exists();
    }

    @Test
    void bounded_context_file_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.html"))))
            .exists();
    }

    @Test
    void core_concept_file_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1.html"))))
            .exists();
    }

    @Test
    void value_object_file_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1.html"))))
            .exists();
    }

    @Test
    void domain_event_file_is_generated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(new File(String.valueOf(Paths.get("target", "candy-doc", "html",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one",
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1.html"))))
            .exists();
    }

    @Test
    void number_of_bounded_contexts_is_correct_in_the_index() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("index.html")
            .getElementsByClass("bounded-context"))
            .hasSize(2);
    }

    @Test
    void number_of_concepts_is_correct_in_the_index() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("index.html")
            .getElementsByClass("concept"))
            .hasSize(4);
    }

    @Test
    void navigation_is_not_rendered_in_the_index_when_bounded_context_is_empty() throws IOException {
        saveDocumentationAsHTML.save(emptyBoundedContext);
        Assertions.assertThat(getDocument("index.html")
            .getElementsByClass("concept"))
            .isEmpty();
    }

    @Test
    void number_of_concepts_in_bounded_context_page_is_correct() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.html")
            .getElementsByClass("interaction"))
            .hasSize(4);
    }

    @Test
    void navigation_is_present_in_bounded_context_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.html")
            .getElementsByClass("navigation"))
            .hasSize(1);
    }

    @Test
    void concept_is_present_in_bounded_context_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.html")
            .getElementsByClass("concept"))
            .hasSize(1);
    }

    @Test
    void navigation_is_present_in_core_concept_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1.html")
            .getElementsByClass("navigation"))
            .hasSize(1);
    }

    @Test
    void concept_is_present_in_core_concept_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1.html")
            .getElementsByClass("concept"))
            .hasSize(1);
    }

    @Test
    void navigation_is_present_in_value_object_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1.html")
            .getElementsByClass("navigation"))
            .hasSize(1);
    }

    @Test
    void concept_is_present_in_value_object_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.ValueObject1.html")
            .getElementsByClass("concept"))
            .hasSize(1);
    }

    @Test
    void navigation_is_present_in_domain_event_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1.html")
            .getElementsByClass("navigation"))
            .hasSize(1);
    }

    @Test
    void concept_is_present_in_domain_event_page() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1.html")
            .getElementsByClass("concept"))
            .hasSize(1);
    }

    @Test
    void interactions_are_not_rendered_when_they_are_empty() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.DomainEvent1.html")
            .getElementsByClass("interaction"))
            .isEmpty();
    }

    @Test
    void number_of_core_concept_interactions_is_correct() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1.html")
            .getElementsByClass("interaction"))
            .hasSize(2);
    }

    @Test
    void concept_titles_are_translated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("index.html")
            .getElementsByClass("concept__name").eachText())
            .containsExactlyInAnyOrder("CoreConcept", "DomainEvent", "ValueObject");
    }

    @Test
    void interaction_is_translated() throws IOException {
        saveDocumentationAsHTML.save(boundedContextsForHTMLGenerationTests);
        Assertions.assertThat(getDocument("candydoc.sample.valid_bounded_contexts.bounded_context_one/" +
            "candydoc.sample.valid_bounded_contexts.bounded_context_one.CoreConcept1.html")
            .getElementsByClass("interaction__title").text())
            .isEqualTo("Interactions");
    }

}
