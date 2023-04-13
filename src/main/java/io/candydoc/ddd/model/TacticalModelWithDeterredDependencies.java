package io.candydoc.ddd.model;

import io.candydoc.ddd.tactical_pattern.TacticalModel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;


@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class TacticalModelWithDeterredDependencies extends TacticalModel {
  abstract public Set<Dependency> deterredDependencies();

  public Set<Dependency> forbiddenDependencies() {
    return dependencies.stream()
        .filter(dependency -> !allowedDependencyPatterns().contains(dependency.pattern()) && !deterredDependencies().contains(dependency))
        .collect(Collectors.toUnmodifiableSet());
  }
}
