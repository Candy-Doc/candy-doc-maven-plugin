package io.candydoc.ddd.tactical_patterns.domain_command;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DomainCommandRulesTest {

  static DomainModel domainCommand;

  @BeforeAll
  static void setUp() {
    domainCommand =
        DomainCommand.builder()
            .fullName(FullName.of("io.candydoc.sample.DomainCommandOne"))
            .simpleName(SimpleName.of("DommainCommandOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.DomainCommandTwo"))
                    .pattern(TacticalPattern.DOMAIN_COMMAND)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.DomainEventOne"))
                    .pattern(TacticalPattern.DOMAIN_EVENT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
                    .pattern(TacticalPattern.ENTITY)
                    .build())
            .build();
  }

  @Test
  void is_a_domain_command() {
    assertThat(domainCommand.dddPattern()).isEqualTo(TacticalPattern.DOMAIN_COMMAND);
  }

  @Test
  void it_can_depend_on_value_object() {
    Dependency valueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(valueObject).isAllowedIn(domainCommand);
  }

  @Test
  void it_can_depend_on_entity() {
    Dependency entity =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
            .pattern(TacticalPattern.ENTITY)
            .build();

    assertThatDependency(entity).isAllowedIn(domainCommand);
  }

  @Test
  void it_can_not_depend_on_another_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainCommandTwo"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isForbiddenIn(DomainCommandRulesTest.domainCommand);
  }

  @Test
  void it_can_not_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isForbiddenIn(domainCommand);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency aggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(aggregate).isForbiddenIn(domainCommand);
  }
}
