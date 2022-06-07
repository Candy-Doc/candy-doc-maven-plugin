package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.AnnotationProcessorConceptFinder;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CoreConceptExtractorTest {

  private final CoreConceptExtractor coreConceptExtractor =
      new CoreConceptExtractor(new AnnotationProcessorConceptFinder());

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
                .simpleName("CoreConcept1")
                .canonicalName(
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
        .extracting("simpleName")
        .containsOnlyOnce("My enum core concept");

    Assertions.assertThat(occurredEvents)
        .contains(
            CoreConceptFound.builder()
                .simpleName("My enum core concept")
                .canonicalName(
                    "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumCoreConcept")
                .description("My enum core concept description")
                .packageName("io.candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("io.candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }
}
