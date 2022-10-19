package io.candydoc.ddd.tactical_patterns.aggregate;

import io.candydoc.ddd.extract_domain_models.DomainModelFinder;
import io.candydoc.ddd.extract_domain_models.ReflectionsDomainModelFinder;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindAggregateTest {

    static DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(List.of("io.candydoc.ddd.tactical_patterns.aggregate"));

    @Test
    public void find_aggregate_from_classes() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"));

        // then
        assertThat(actualDomainModels)
            .contains(Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.AggregateOne"))
                .simpleName(SimpleName.of("My aggregate"))
                .description(Description.of("Describe the aggregate here"))
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.ValueObjectOne"))
                    .build())
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.DomainCommandOne"))
                    .build())
                .dependency(Dependency.builder()
                    .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
                    .name(CanonicalName.of("io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.DomainEventOne"))
                    .build())
                .build());
    }

    @Test
    public void attributes_not_provided() {
        // when
        Set<DomainModel> actualDomainModels = domainModelFinder.findDomainModels(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"));

        // then
        assertThat(actualDomainModels)
            .contains(Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"))
                .canonicalName(CanonicalName.of("io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.AggregateTwo"))
                .simpleName(SimpleName.of("AggregateTwo"))
                .description(Description.of(""))
                .build());
    }

    @io.candydoc.ddd.annotations.Aggregate(name = "My aggregate", description = "Describe the aggregate here")
    public static class AggregateOne {
        private ValueObjectOne valueObjectOne;

        public DomainEventOne handle(DomainCommandOne command) {
            return new DomainEventOne();
        }
    }

    @io.candydoc.ddd.annotations.DomainCommand
    public static class DomainCommandOne {
    }

    @io.candydoc.ddd.annotations.DomainEvent
    public static class DomainEventOne {
    }

    @io.candydoc.ddd.annotations.ValueObject
    public static class ValueObjectOne {
    }

    @io.candydoc.ddd.annotations.Aggregate
    public static class AggregateTwo {
    }

}
