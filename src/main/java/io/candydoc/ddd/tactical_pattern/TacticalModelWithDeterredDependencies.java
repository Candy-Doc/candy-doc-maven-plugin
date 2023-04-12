package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class TacticalModelWithDeterredDependencies extends TacticalModel{

  public Set<Dependency> deterredDependencies() {
    return this.dependencies.stream()
        .filter(dependency -> deterredDependenciesPatterns().contains(dependency.pattern()))
        .collect(Collectors.toUnmodifiableSet());
  }
  public abstract Set<DDDPattern> deterredDependenciesPatterns();
}
