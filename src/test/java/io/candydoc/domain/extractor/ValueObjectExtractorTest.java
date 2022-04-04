package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueObjectExtractorTest {

  private final ValueObjectExtractor valueObjectExtractor = new ValueObjectExtractor(new ReflectionsConceptFinder());

  @Test
  public void anonymous_value_object_are_skipped() {
    // given
    ExtractValueObjects command =
        ExtractValueObjects.builder()
            .packageToScan("candydoc.sample.concepts_with_deducted_annotations")
            .build();

    // when
    List<DomainEvent> occurredEvents = valueObjectExtractor.extract(command);

    // then
    Assertions.assertThat(occurredEvents)
        .filteredOn(ValueObjectFound.class::isInstance)
        .extracting("name")
        .containsOnlyOnce("EnumValueObject");

    Assertions.assertThat(occurredEvents)
        .contains(
            ValueObjectFound.builder()
                .name("EnumValueObject")
                .className(
                    "candydoc.sample.concepts_with_deducted_annotations.sub_package.EnumValueObject")
                .description("My enum value object description")
                .packageName("candydoc.sample.concepts_with_deducted_annotations.sub_package")
                .boundedContext("candydoc.sample.concepts_with_deducted_annotations")
                .build());
  }
}
