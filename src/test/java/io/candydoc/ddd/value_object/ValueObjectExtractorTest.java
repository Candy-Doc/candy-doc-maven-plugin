package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.ReflectionsConceptFinder;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueObjectExtractorTest {

  private final ValueObjectExtractor valueObjectExtractor =
      new ValueObjectExtractor(new ReflectionsConceptFinder());

  @Test
  public void anonymous_value_object_are_skipped() {
    // given
    ExtractValueObjects command =
        ExtractValueObjects.builder()
            .packageToScan("io.candydoc.sample.concepts_with_deducted_annotations")
            .build();

    // when
    List<Event> occurredEvents = valueObjectExtractor.extract(command);

    // then
    Assertions.assertThat(occurredEvents)
        .filteredOn(ValueObjectFound.class::isInstance)
        .extracting("name")
        .containsOnlyOnce("EnumValueObject");

    Assertions.assertThat(occurredEvents)
        .contains(
            ValueObjectFound.builder()
                .simpleName("EnumValueObject")
                .canonicalName(
                    "io.candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumValueObject")
                .description("My enum value object description")
                .packageName("io.candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("io.candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }
}
