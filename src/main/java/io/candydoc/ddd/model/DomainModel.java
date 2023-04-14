package io.candydoc.ddd.model;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public abstract class DomainModel {
  private final CanonicalName canonicalName;
  private final SimpleName simpleName;
  private final PackageName packageName;
  private final Description description;

  @Singular
  @Getter(AccessLevel.PRIVATE)
  private final Set<Dependency> dependencies;

  public boolean dependsOn(CanonicalName canonicalName) {
    return dependencies.stream().anyMatch(dependency -> dependency.name().equals(canonicalName));
  }

  public Set<Dependency> allowedDependencies() {
    return dependencies.stream()
        .filter(dependency -> allowedDependencyPatterns().contains(dependency.pattern()))
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<Dependency> forbiddenDependencies() {
    return dependencies.stream()
        .filter(dependency -> !allowedDependencyPatterns().contains(dependency.pattern()))
        .collect(Collectors.toUnmodifiableSet());
  }

  public boolean hasForbiddenDependencies() {
    return !forbiddenDependencies().isEmpty();
  }

  public abstract DDDPattern dddPattern();

  public abstract Set<DDDPattern> allowedDependencyPatterns();
}
