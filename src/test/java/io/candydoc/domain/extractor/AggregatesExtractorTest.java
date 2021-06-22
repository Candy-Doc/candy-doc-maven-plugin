package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractAggregates;
import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.DomainEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AggregatesExtractorTest {

    private final AggregatesExtractor aggregatesExtractor = new AggregatesExtractor();

    @Test
    public void use_simple_class_name_when_name_is_empty() {
        // given
        ExtractAggregates command = ExtractAggregates.builder()
            .packageToScan("candydoc.sample.concepts_with_deducted_annotations")
            .build();

        // when
        List<DomainEvent> occurredEvents = aggregatesExtractor.extract(command);

        // then
        Assertions.assertThat(occurredEvents)
            .contains(AggregateFound.builder()
                    .name("Aggregate1")
                    .className("candydoc.sample.concepts_with_deducted_annotations.sub_package.Aggregate1")
                    .description("")
                    .packageName("candydoc.sample.concepts_with_deducted_annotations.sub_package")
                    .boundedContext("candydoc.sample.concepts_with_deducted_annotations")
                    .build(),
                AggregateFound.builder()
                    .name("My aggregate 2")
                    .className("candydoc.sample.concepts_with_deducted_annotations.sub_package.Aggregate2")
                    .description("My aggregate description 2")
                    .packageName("candydoc.sample.concepts_with_deducted_annotations.sub_package")
                    .boundedContext("candydoc.sample.concepts_with_deducted_annotations")
                    .build());
    }


}
