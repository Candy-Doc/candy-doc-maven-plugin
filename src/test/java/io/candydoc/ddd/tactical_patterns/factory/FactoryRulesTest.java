package io.candydoc.ddd.tactical_patterns.factory;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.Factory;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FactoryRulesTest {
  static DomainModel factory;

  @BeforeAll
  static void setUp() {
    factory =
        Factory.builder()
            .canonicalName(CanonicalName.of("io.candydoc.sample.RepositoryOne"))
            .simpleName(SimpleName.of("FactoryOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainEntityOne"))
                    .pattern(TacticalPattern.DOMAIN_ENTITY)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.DomainServiceOne"))
                    .pattern(TacticalPattern.DOMAIN_SERVICE)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.RepositoryOne"))
                    .pattern(TacticalPattern.REPOSITORY)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(CanonicalName.of("io.candydoc.sample.FactoryTwo"))
                    .pattern(TacticalPattern.FACTORY)
                    .build())
            .build();
  }

  @Test
  void is_a_factory() {
    assertThat(factory.dddPattern()).isEqualTo(TacticalPattern.FACTORY);
  }

  @Test
  void it_can_depend_on_aggregate() {
    Dependency aggregate =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(aggregate).isAllowedIn(factory);
  }

  @Test
  void it_can_depend_on_repository() {
    Dependency repository =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.RepositoryOne"))
            .pattern(TacticalPattern.REPOSITORY)
            .build();

    assertThatDependency(repository).isAllowedIn(factory);
  }

  @Test
  void it_can_depend_on_domain_service() {
    Dependency domain_service =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainServiceOne"))
            .pattern(TacticalPattern.DOMAIN_SERVICE)
            .build();

    assertThatDependency(domain_service).isAllowedIn(factory);
  }

  @Test
  void it_can_depend_on_another_factory() {
    Dependency another_factory =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.FactoryTwo"))
            .pattern(TacticalPattern.FACTORY)
            .build();

    assertThatDependency(another_factory).isAllowedIn(factory);
  }

  @Test
  void it_can_not_depend_on_value_object() {
    Dependency valueObject =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(valueObject).isForbiddenIn(factory);
  }

  @Test
  void it_can_not_depend_on_entity() {
    Dependency entity =
        Dependency.builder()
            .name(CanonicalName.of("io.candydoc.sample.DomainEntityOne"))
            .pattern(TacticalPattern.DOMAIN_ENTITY)
            .build();

    assertThatDependency(entity).isForbiddenIn(factory);
  }
}
