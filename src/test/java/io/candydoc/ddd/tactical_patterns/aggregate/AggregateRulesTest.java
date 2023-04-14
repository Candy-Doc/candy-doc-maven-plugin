package io.candydoc.ddd.tactical_patterns.aggregate;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AggregateRulesTest {

  static DomainModel aggregate;

  @BeforeAll
  static void setUp() {
    aggregate =
        Aggregate.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .simpleName(SimpleName.of("AggregateOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
                    .pattern(TacticalPattern.DOMAIN_COMMAND)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
                    .pattern(TacticalPattern.DOMAIN_EVENT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.AggregateTwo"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
                    .pattern(TacticalPattern.DOMAIN_ENTITY)
                    .build())
            .build();
  }

  @Test
  void is_an_aggregate() {
    assertThat(aggregate.dddPattern()).isEqualTo(TacticalPattern.AGGREGATE);
  }

  @Test
  void it_can_depend_on_value_object() {
    Dependency valueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(valueObject).isAllowedIn(aggregate);
  }

  @Test
  void it_can_depend_on_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isAllowedIn(aggregate);
  }

  @Test
  void it_can_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isAllowedIn(aggregate);
  }

  @Test
  void it_can_depend_on_entity() {
    Dependency entity =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
            .pattern(TacticalPattern.DOMAIN_ENTITY)
            .build();

    assertThatDependency(entity).isAllowedIn(aggregate);
  }

  @Test
  void it_can_not_depend_on_another_aggregate() {
    Dependency anotherAggregate =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateTwo"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(anotherAggregate).isForbiddenIn(aggregate);
  }
}
