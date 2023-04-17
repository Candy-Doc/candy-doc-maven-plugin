package io.candydoc.ddd.tactical_patterns.domain_service;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainService;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DomainServiceRulesTest {
  static DomainModel service;

  @BeforeAll
  static void setUp() {
    service =
        DomainService.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.DomainServiceOne"))
            .simpleName(SimpleName.of("DomainServiceOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.RepositoryOne"))
                    .pattern(TacticalPattern.REPOSITORY)
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
                    .name(CanonicalName.of("io.candydoc.sample.DomainServiceTwo"))
                    .pattern(TacticalPattern.DOMAIN_SERVICE)
                    .build())
            .build();
  }

  @Test
  void is_an_domain_service() {
    assertThat(service.dddPattern()).isEqualTo(TacticalPattern.DOMAIN_SERVICE);
  }

  @Test
  void it_can_depend_on_other_domain_service() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainServiceTwo"))
            .pattern(TacticalPattern.DOMAIN_SERVICE)
            .build();

    assertThatDependency(anotherValueObject).isAllowedIn(service);
  }

  @Test
  void it_can_not_depend_on_value_object() {
    Dependency anotherValueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(anotherValueObject).isForbiddenIn(service);
  }

  @Test
  void it_can_depend_on_domain_event() {
    Dependency domainEvent =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEventOne"))
            .pattern(TacticalPattern.DOMAIN_EVENT)
            .build();

    assertThatDependency(domainEvent).isAllowedIn(service);
  }

  @Test
  void it_can_not_depend_on_aggregate() {
    Dependency anotherAggregate =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(anotherAggregate).isForbiddenIn(service);
  }
}
