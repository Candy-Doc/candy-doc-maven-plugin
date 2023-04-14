package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Factory extends TacticalModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(TacticalPattern.AGGREGATE);

  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.FACTORY;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }
}
