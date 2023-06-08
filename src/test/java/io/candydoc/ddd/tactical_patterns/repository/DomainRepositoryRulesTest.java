package io.candydoc.ddd.tactical_patterns.repository;

import static io.candydoc.ddd.AssertDependency.assertThatDependency;
import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.DomainRepository;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class DomainRepositoryRulesTest {

  static DomainModel repository;

  @BeforeAll
  static void setUp() {
    repository =
        DomainRepository.builder()
            .fullName(FullName.of("io.candydoc.sample.RepositoryOne"))
            .simpleName(SimpleName.of("RepositoryOne"))
            .packageName(PackageName.of("io.candydoc.sample"))
            .dependencies(
                Arrays.stream(TacticalPattern.values())
                    .filter(tacticalPattern -> !tacticalPattern.equals(TacticalPattern.AGGREGATE))
                    .map(
                        tacticalPattern ->
                            Dependency.builder()
                                .name(
                                    FullName.of(
                                        "io.candydoc.sample.OtherTacticalPatternThanAggregate"))
                                .pattern(tacticalPattern)
                                .build())
                    .collect(Collectors.toUnmodifiableSet()))
            .dependencies(
                Arrays.stream(StrategicPattern.values())
                    .map(
                        strategicPattern ->
                            Dependency.builder()
                                .name(FullName.of("io.candydoc.sample.StrategicPattern"))
                                .pattern(strategicPattern)
                                .build())
                    .collect(Collectors.toUnmodifiableSet()))
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

  @ParameterizedTest
  @EnumSource(
      value = TacticalPattern.class,
      names = {"AGGREGATE"},
      mode = EnumSource.Mode.EXCLUDE)
  void it_can_not_depend_on_other_tactical_pattern_than_aggregate(TacticalPattern tacticalPattern) {
    Dependency otherTacticalPatternThanAggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.OtherTacticalPatternThanAggregate"))
            .pattern(tacticalPattern)
            .build();

    assertThatDependency(otherTacticalPatternThanAggregate).isForbiddenIn(repository);
  }

  @ParameterizedTest
  @EnumSource(StrategicPattern.class)
  void it_can_not_depend_on_strategic_patterns(StrategicPattern strategicPattern) {
    Dependency otherTacticalPatternThanAggregate =
        Dependency.builder()
            .name(FullName.of("io.candydoc.sample.StrategicPattern"))
            .pattern(strategicPattern)
            .build();

    assertThatDependency(otherTacticalPatternThanAggregate).isForbiddenIn(repository);
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
