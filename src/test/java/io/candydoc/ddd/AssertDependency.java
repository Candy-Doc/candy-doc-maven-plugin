package io.candydoc.ddd;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.TacticalModelWithDeterredDependencies;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertDependency {

  private static final String ALLOWED_LABEL = "Allowed dependencies";
  private static final String DETERRED_LABEL = "Allowed but not recommended dependencies";
  private static final String FORBIDDEN_LABEL = "Forbidden dependencies";

  private final Dependency dependency;

  public AssertDependency isAllowedIn(DomainModel domainModel) {
    assertThat(domainModel.allowedDependencies()).as(ALLOWED_LABEL).contains(dependency);

    assertThat(domainModel.forbiddenDependencies()).as(FORBIDDEN_LABEL).doesNotContain(dependency);

    return this;
  }

  public AssertDependency isForbiddenIn(DomainModel domainModel) {
    assertThat(domainModel.allowedDependencies()).as(ALLOWED_LABEL).doesNotContain(dependency);

    assertThat(domainModel.forbiddenDependencies()).as(FORBIDDEN_LABEL).contains(dependency);

    return this;
  }

  public static AssertDependency assertThatDependency(Dependency dependency) {
    return new AssertDependency(dependency);
  }

  public AssertDependency isDeterredIn(TacticalModelWithDeterredDependencies tacticalModelWithDeterredDependencies) {
    assertThat(tacticalModelWithDeterredDependencies.deterredDependencies()).as(DETERRED_LABEL).contains(dependency);
    assertThat(tacticalModelWithDeterredDependencies.allowedDependencies()).as(ALLOWED_LABEL).contains(dependency);
    return this;
  }
}
