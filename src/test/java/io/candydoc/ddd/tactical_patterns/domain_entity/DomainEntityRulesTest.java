package io.candydoc.ddd.tactical_patterns.domain_entity;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.DomainEntity;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DomainEntityRulesTest {
  static DomainModel entity;

  @BeforeAll
  static void setUp() {
    entity =
        DomainEntity.builder()
            .fullName(FullName.of("io.candydoc.sample.EntityOne"))
            .simpleName(SimpleName.of("EntityOne"))
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
                    .name(FullName.of("io.candydoc.sample.EntityTwo"))
                    .pattern(TacticalPattern.DOMAIN_ENTITY)
                    .build())
            .build();
  }

  @Test
  void is_a_domain_entity() {
    assertThat(entity.dddPattern()).isEqualTo(TacticalPattern.DOMAIN_ENTITY);
  }

  @Test
  void it_can_depend_on_other_domain_entity() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.EntityTwo"))
            .pattern(TacticalPattern.DOMAIN_ENTITY)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(entity);
  }

  @Test
  void it_can_depend_on_value_object() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(entity);
  }

  @Test
  void it_can_not_depend_on_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isForbiddenIn(entity);
  }

  @Test
  void it_can_not_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isForbiddenIn(entity);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency anotherAggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(anotherAggregate).isForbiddenIn(entity);
  }
}
