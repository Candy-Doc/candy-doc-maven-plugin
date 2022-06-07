package io.candydoc.ddd.aggregate;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.AnnotationProcessorConceptFinder;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AggregatesExtractorTest {

  private final AggregatesExtractor aggregatesExtractor =
      new AggregatesExtractor(new AnnotationProcessorConceptFinder());

  @Test
  public void use_simple_class_name_when_name_is_empty() {
    // given
    ExtractAggregates command =
        ExtractAggregates.builder()
            .packageToScan("io.candydoc.sample.concepts_with_deducted_annotations")
            .build();

    // when
    List<Event> occurredEvents = aggregatesExtractor.extract(command);

    // then
    Assertions.assertThat(occurredEvents)
        .contains(
            AggregateFound.builder()
                .simpleName("Aggregate1")
                .canonicalName(
                    "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.Aggregate1")
                .description("")
                .packageName("io.candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("io.candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }
}
