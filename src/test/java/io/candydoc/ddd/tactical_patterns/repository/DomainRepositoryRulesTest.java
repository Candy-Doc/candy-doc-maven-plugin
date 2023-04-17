package io.candydoc.ddd.tactical_patterns.repository;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.tactical_pattern.DomainRepository;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DomainRepositoryRulesTest {

  static DomainModel repository;

  @BeforeAll
  static void setUp() {
    repository =
        DomainRepository.builder()
            .fullName(FullName.of("io.candydoc.sample.RepositoryOne"))
            .simpleName(SimpleName.of("RepositoryOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
                    .pattern(TacticalPattern.VALUE_OBJECT)
                    .build())
            .dependency(
                Dependency.builder()
                    .name(FullName.of("io.candydoc.sample.AggregateOne"))
                    .pattern(TacticalPattern.AGGREGATE)
                    .build())
            .build();
  }

  @Test
  void is_a_repository() {
    assertThat(repository.dddPattern()).isEqualTo(TacticalPattern.DOMAIN_REPOSITORY);
  }

  @Test
  void it_can_not_depend_on_value_object() {
    Dependency valueObject =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.ValueObjectOne"))
            .pattern(TacticalPattern.VALUE_OBJECT)
            .build();

    assertThatDependency(valueObject).isForbiddenIn(repository);
  }

  @Test
  void it_can_depend_on_aggregate() {
    Dependency aggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.AggregateOne"))
            .pattern(TacticalPattern.AGGREGATE)
            .build();

    assertThatDependency(aggregate).isAllowedIn(repository);
  }
}
