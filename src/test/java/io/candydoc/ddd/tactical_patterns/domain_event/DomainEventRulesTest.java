package io.candydoc.ddd.tactical_patterns.domain_event;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

public class DomainEventRulesTest {

    static DomainModel domainEvent;

    @BeforeAll
    static void setUp() {
        domainEvent = DomainEvent.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .simpleName(SimpleName.of("DommainEventOne"))
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
                .name(CanonicalName.of("io.candydoc.sample.DomainEventTwo"))
                .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                .pattern(DDDPattern.Tactical.AGGREGATE)
                .build())
            .build();
    }

    @Test
    void is_a_domain_event() {
        assertThat(domainEvent.dddPattern())
            .isEqualTo(DDDPattern.Tactical.DOMAIN_EVENT);
    }

    @Test
    void it_can_depend_on_value_object() {
        Dependency valueObject = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(DDDPattern.Tactical.VALUE_OBJECT)
            .build();

        assertThatDependency(valueObject)
            .isAllowedIn(domainEvent);
    }

    @Test
    void it_can_not_depend_on_domain_command() {
        Dependency domainCommand = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
            .build();

        assertThatDependency(domainCommand)
            .isForbiddenIn(DomainEventRulesTest.domainEvent);
    }

    @Test
    void it_can_not_depend_on_another_domain_event() {
        Dependency domainEvent = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventTwo"))
            .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
            .build();

        assertThatDependency(domainEvent)
            .isForbiddenIn(DomainEventRulesTest.domainEvent);
    }

    @Test
    void it_can_not_depend_on_aggregate() {
        Dependency aggregate = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(DDDPattern.Tactical.AGGREGATE)
            .build();

        assertThatDependency(aggregate)
            .isForbiddenIn(domainEvent);
    }

}
