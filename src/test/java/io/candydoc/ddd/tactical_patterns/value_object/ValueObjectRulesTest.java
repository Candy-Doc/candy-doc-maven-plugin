package io.candydoc.ddd.tactical_patterns.value_object;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

public class ValueObjectRulesTest {

    static DomainModel valueObject;

    @BeforeAll
    static void setUp() {
        valueObject = ValueObject.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .simpleName(SimpleName.of("ValueObjectOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.ValueObjectTwo"))
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
                .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                .pattern(DDDPattern.Tactical.AGGREGATE)
                .build())
            .build();
    }

    @Test
    void is_a_value_object() {
        assertThat(valueObject.dddPattern())
            .isEqualTo(DDDPattern.Tactical.VALUE_OBJECT);
    }

    @Test
    void it_can_depend_on_other_value_object() {
        Dependency anotherValueObject = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectTwo"))
            .pattern(DDDPattern.Tactical.VALUE_OBJECT)
            .build();

        assertThatDependency(anotherValueObject)
            .isAllowedIn(valueObject);
    }

    @Test
    void it_can_not_depend_on_domain_command() {
        Dependency domainCommand = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
            .build();

        assertThatDependency(domainCommand)
            .isForbiddenIn(valueObject);
    }

    @Test
    void it_can_not_depend_on_domain_event() {
        Dependency domainEvent = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
            .build();

        assertThatDependency(domainEvent)
            .isForbiddenIn(valueObject);
    }

    @Test
    void it_can_not_depend_on_aggregate() {
        Dependency anotherAggregate = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(DDDPattern.Tactical.AGGREGATE)
            .build();

        assertThatDependency(anotherAggregate)
            .isForbiddenIn(valueObject);
    }

}
