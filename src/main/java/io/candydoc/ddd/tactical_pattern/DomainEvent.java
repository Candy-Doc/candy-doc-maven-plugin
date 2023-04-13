package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.DependencyVisitor;
import io.candydoc.ddd.model.TacticalModelWithDeterredDependencies;
import io.candydoc.plugin.model.DependencyDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainEvent extends TacticalModelWithDeterredDependencies {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(TacticalPattern.VALUE_OBJECT);

  private static final Set<DDDPattern> DETERRED_DEPENDENCIES = Set.of(TacticalPattern.ENTITY);

  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.DOMAIN_EVENT;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

  @Override
  public Set<Dependency> deterredDependencies() {
    return this.dependencies.stream()
        .filter(dependency -> DETERRED_DEPENDENCIES.contains(dependency.pattern()))
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public List<DependencyDto> accept(DependencyVisitor visitor) {
    return visitor.visit(this);
  }
}
