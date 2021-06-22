package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcepts;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CoreConceptExtractorTest {

    private final CoreConceptExtractor coreConceptExtractor = new CoreConceptExtractor();

    @Test
    public void use_simple_class_name_when_name_is_empty() {
        // given
        ExtractCoreConcepts command = ExtractCoreConcepts.builder()
            .packageToScan("candydoc.sample.concepts_with_deducted_annotations")
            .build();

        // when
        List<DomainEvent> occurredEvents = coreConceptExtractor.extract(command);

        // then
        Assertions.assertThat(occurredEvents)
            .contains(CoreConceptFound.builder()
                    .name("CoreConcept1")
                    .className("candydoc.sample.concepts_with_deducted_annotations.sub_package.CoreConcept1")
                    .description("")
                    .packageName("candydoc.sample.concepts_with_deducted_annotations.sub_package")
                    .boundedContext("candydoc.sample.concepts_with_deducted_annotations")
                    .build(),
                CoreConceptFound.builder()
                    .name("My core concept 2")
                    .className("candydoc.sample.concepts_with_deducted_annotations.sub_package.CoreConcept2")
                    .description("My core concept description 2")
                    .packageName("candydoc.sample.concepts_with_deducted_annotations.sub_package")
                    .boundedContext("candydoc.sample.concepts_with_deducted_annotations")
                    .build());
    }

}
