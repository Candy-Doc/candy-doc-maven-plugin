package io.candydoc.ddd.tactical_patterns.aggregate;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

public class AggregateRulesTest {

    static DomainModel aggregate;

    @BeforeAll
    static void setUp() {
        aggregate = Aggregate.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .simpleName(SimpleName.of("AggregateOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.AggregateTwo"))
                .pattern(DDDPattern.Tactical.AGGREGATE)
                .build())
            .build();
    }

    @Test
    void is_an_aggregate() {
        assertThat(aggregate.dddPattern())
            .isEqualTo(DDDPattern.Tactical.AGGREGATE);
    }

    @Test
    void it_can_depend_on_value_object() {
        Dependency valueObject = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(DDDPattern.Tactical.VALUE_OBJECT)
            .build();

        assertThatDependency(valueObject)
            .isAllowedIn(aggregate);
    }

    @Test
    void it_can_depend_on_domain_command() {
        Dependency domainCommand = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
            .build();

        assertThatDependency(domainCommand)
            .isAllowedIn(aggregate);
    }

    @Test
    void it_can_depend_on_domain_event() {
        Dependency domainEvent = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
            .build();

        assertThatDependency(domainEvent)
            .isAllowedIn(aggregate);
    }

    @Test
    void it_can_not_depend_on_another_aggregate() {
        Dependency anotherAggregate = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateTwo"))
            .pattern(DDDPattern.Tactical.AGGREGATE)
            .build();

        assertThatDependency(anotherAggregate)
            .isForbiddenIn(aggregate);
    }

}
