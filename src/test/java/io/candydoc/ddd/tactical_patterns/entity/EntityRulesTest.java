package io.candydoc.ddd.tactical_patterns.entity;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.Entity;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

public class EntityRulesTest {
  static DomainModel entity;

  @BeforeAll
  static void setUp() {
    entity =
        Entity.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.EntityOne"))
            .simpleName(SimpleName.of("EntityOne"))
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
                    .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.EntityTwo"))
                    .pattern(TacticalPattern.ENTITY)
                    .build())
            .build();
  }
  @Test
  void is_an_entity() {
    assertThat(entity.dddPattern()).isEqualTo(TacticalPattern.ENTITY);
  }

  @Test
  void it_can_depend_on_other_entity() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.EntityTwo"))
            .pattern(TacticalPattern.ENTITY)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(entity);
  }

  @Test
  void it_can_depend_on_value_object() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(entity);
  }

  @Test
  void it_can_not_depend_on_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isForbiddenIn(entity);
  }

  @Test
  void it_can_not_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isForbiddenIn(entity);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency anotherAggregate =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(anotherAggregate).isForbiddenIn(entity);
  }
}
