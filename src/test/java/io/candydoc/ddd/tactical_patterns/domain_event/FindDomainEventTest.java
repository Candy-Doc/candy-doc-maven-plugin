package io.candydoc.ddd.tactical_patterns.domain_event;

import io.candydoc.ddd.extract_domain_models.DomainModelFinder;
import io.candydoc.ddd.extract_domain_models.ReflectionsDomainModelFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindDomainEventTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.tactical_patterns.domain_event"));

    @Test
    public void find_domain_event_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"));

        // then
        assertThat(actualDomainModels)
            .contains(DomainEvent.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest.DomainEventOne"))
                .simpleName(SimpleName.of("DomainEventOne"))
                .description(Description.of("Describe the domain event here"))
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest.ValueObjectOne"))
                    .build())
                .build());
    }

    @Test
    public void attributes_not_provided() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"));

        // then
        assertThat(actualDomainModels)
            .contains(DomainEvent.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest.DomainEventTwo"))
                .simpleName(SimpleName.of("DomainEventTwo"))
                .description(Description.of(""))
                .build());
    }

    @io.candydoc.ddd.annotations.DomainEvent(description = "Describe the domain event here")
    public static class DomainEventOne {
        private ValueObjectOne valueObjectOne;
    }

    @io.candydoc.ddd.annotations.ValueObject
    public static class ValueObjectOne {
    }

    @io.candydoc.ddd.annotations.DomainEvent
    public static class DomainEventTwo {
    }

}
