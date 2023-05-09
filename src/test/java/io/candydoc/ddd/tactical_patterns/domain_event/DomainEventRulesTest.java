package io.candydoc.ddd.tactical_patterns.domain_event;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DomainEventRulesTest {

  static DomainModel domainEvent;

  @BeforeAll
  static void setUp() {
    domainEvent =
        DomainEvent.builder()
            .fullName(FullName.of("io.candydoc.sample.DomainEventOne"))
            .simpleName(SimpleName.of("DommainEventOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.DomainCommandOne"))
                    .pattern(TacticalPattern.DOMAIN_COMMAND)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.DomainEventTwo"))
                    .pattern(TacticalPattern.DOMAIN_EVENT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.EntityOne"))
                    .pattern(TacticalPattern.DOMAIN_ENTITY)
                    .build())
            .build();
  }

  @Test
  void is_a_domain_event() {
    assertThat(domainEvent.dddPattern()).isEqualTo(TacticalPattern.DOMAIN_EVENT);
  }

  @Test
  void it_can_depend_on_value_object() {
    Dependency valueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(valueObject).isAllowedIn(domainEvent);
  }

  @Test
  void it_can_not_depend_on_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isForbiddenIn(DomainEventRulesTest.domainEvent);
  }

  @Test
  void it_can_not_depend_on_another_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainEventTwo"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isForbiddenIn(DomainEventRulesTest.domainEvent);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency aggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(aggregate).isForbiddenIn(domainEvent);
  }

  @Test
  void it_can_depend_on_domain_entity() {
    Dependency entity =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.EntityOne"))
            .pattern(TacticalPattern.DOMAIN_ENTITY)
            .build();

    assertThatDependency(entity).isAllowedIn(domainEvent);
  }
}
