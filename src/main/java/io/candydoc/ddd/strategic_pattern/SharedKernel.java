package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.DependencyVisitor;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import io.candydoc.plugin.model.DependencyDto;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SharedKernel extends StrategicModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(TacticalPattern.VALUE_OBJECT);

  @Override
  public DDDPattern dddPattern() {
    return StrategicPattern.SHARED_KERNEL;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.sharedKernel(this);
  }

  @Override
  public List<DependencyDto> accept(DependencyVisitor visitor) {
    return visitor.visit(this);
  }
}
