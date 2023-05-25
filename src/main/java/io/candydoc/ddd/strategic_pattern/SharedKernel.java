package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.model.Relations;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SharedKernel extends StrategicModel {

  @Accessors(fluent = true)
  @Getter
  @Singular
  private final Set<Relations> relations;

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
}
