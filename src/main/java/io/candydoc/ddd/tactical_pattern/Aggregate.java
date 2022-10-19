package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
public class Aggregate extends TacticalPattern {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
      DDDPattern.Tactical.DOMAIN_COMMAND,
      DDDPattern.Tactical.DOMAIN_EVENT,
      DDDPattern.Tactical.VALUE_OBJECT
  );

  @Override
  public DDDPattern dddPattern() {
    return DDDPattern.Tactical.AGGREGATE;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

}
