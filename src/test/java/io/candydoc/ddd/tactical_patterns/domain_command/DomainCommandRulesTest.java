package io.candydoc.ddd.tactical_patterns.domain_command;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

public class DomainCommandRulesTest {

    static DomainModel domainCommand;

    @BeforeAll
    static void setUp() {
        domainCommand = DomainCommand.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .simpleName(SimpleName.of("DommainCommandOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                .pattern(DDDPattern.Tactical.VALUE_OBJECT)
                .build())
            .dependency(Dependency.builder()
                .name(CanonicalName.of("io.candydoc.sample.DomainCommandTwo"))
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
    void is_a_domain_command() {
        assertThat(domainCommand.dddPattern())
            .isEqualTo(DDDPattern.Tactical.DOMAIN_COMMAND);
    }

    @Test
    void it_can_depend_on_value_object() {
        Dependency valueObject = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(DDDPattern.Tactical.VALUE_OBJECT)
            .build();

        assertThatDependency(valueObject)
            .isAllowedIn(domainCommand);
    }

    @Test
    void it_can_not_depend_on_another_domain_command() {
        Dependency domainCommand = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandTwo"))
            .pattern(DDDPattern.Tactical.DOMAIN_COMMAND)
            .build();

        assertThatDependency(domainCommand)
            .isForbiddenIn(DomainCommandRulesTest.domainCommand);
    }

    @Test
    void it_can_not_depend_on_domain_event() {
        Dependency domainEvent = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(DDDPattern.Tactical.DOMAIN_EVENT)
            .build();

        assertThatDependency(domainEvent)
            .isForbiddenIn(domainCommand);
    }

    @Test
    void it_can_not_depend_on_aggregate() {
        Dependency aggregate = Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(DDDPattern.Tactical.AGGREGATE)
            .build();

        assertThatDependency(aggregate)
            .isForbiddenIn(domainCommand);
    }

}
