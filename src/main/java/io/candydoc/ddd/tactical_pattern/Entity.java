package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.DependencyVisitor;
import io.candydoc.plugin.model.DependencyDto;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Entity extends TacticalModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES =
      Set.of(TacticalPattern.ENTITY, TacticalPattern.VALUE_OBJECT);

  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.ENTITY;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

  @Override
  public List<DependencyDto> accept(DependencyVisitor visitor) {
    return visitor.visit(this);
  }
}
