package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.ReflectionsConceptFinder;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CoreConceptExtractorTest {

  private final CoreConceptExtractor coreConceptExtractor =
      new CoreConceptExtractor(new ReflectionsConceptFinder());

  @Test
  public void use_simple_class_name_when_name_is_empty() {
    // given
    ExtractCoreConcepts command =
        ExtractCoreConcepts.builder()
            .packageToScan("io.candydoc.sample.concepts_with_deducted_annotations")
            .build();

    // when
    List<Event> occurredEvents = coreConceptExtractor.extract(command);

    // then
    Assertions.assertThat(occurredEvents)
        .contains(
            CoreConceptFound.builder()
                .name("CoreConcept1")
                .className(
                    "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.CoreConcept1")
                .description("")
                .packageName("io.candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("io.candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }

  @Test
  public void anonymous_core_concepts_are_skipped() {
    // given
    ExtractCoreConcepts command =
        ExtractCoreConcepts.builder()
            .packageToScan("io.candydoc.sample.concepts_with_deducted_annotations")
            .build();

    // when
    List<Event> occurredEvents = coreConceptExtractor.extract(command);

    // then
    Assertions.assertThat(occurredEvents)
        .filteredOn(CoreConceptFound.class::isInstance)
        .extracting("name")
        .containsOnlyOnce("My enum core concept");

    Assertions.assertThat(occurredEvents)
        .contains(
            CoreConceptFound.builder()
                .name("My enum core concept")
                .className(
                    "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumCoreConcept")
                .description("My enum core concept description")
                .packageName("io.candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("io.candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }
}
