package io.candydoc.ddd.tactical_patterns.value_object;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ValueObjectRulesTest {

  static DomainModel valueObject;

  @BeforeAll
  static void setUp() {
    valueObject =
        ValueObject.builder()
            .fullName(FullName.of("io.candydoc.sample.ValueObjectOne"))
            .simpleName(SimpleName.of("ValueObjectOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.ValueObjectTwo"))
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
                    .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
                    .pattern(TacticalPattern.ENTITY)
                    .build())
            .build();
  }

  @Test
  void is_a_value_object() {
    assertThat(valueObject.dddPattern()).isEqualTo(TacticalPattern.VALUE_OBJECT);
  }

  @Test
  void it_can_depend_on_other_value_object() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.ValueObjectTwo"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(valueObject);
  }

  @Test
  void it_can_not_depend_on_domain_command() {
    Dependency domainCommand =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainCommandOne"))
            .pattern(TacticalPattern.DOMAIN_COMMAND)
            .build();

    assertThatDependency(domainCommand).isForbiddenIn(valueObject);
  }

  @Test
  void it_can_not_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isForbiddenIn(valueObject);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency anotherAggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(anotherAggregate).isForbiddenIn(valueObject);
  }

  @Test
  void it_can_not_depend_on_entity() {
    Dependency entity =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.EntityOne"))
            .pattern(TacticalPattern.ENTITY)
            .build();

    assertThatDependency(entity).isForbiddenIn(valueObject);
  }
}
