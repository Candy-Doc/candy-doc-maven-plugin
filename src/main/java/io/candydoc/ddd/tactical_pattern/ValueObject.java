package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.DDDPattern;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
public class ValueObject extends DomainModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
      DDDPattern.Tactical.VALUE_OBJECT
  );

  @Override
  public DDDPattern dddPattern() {
    return DDDPattern.Tactical.VALUE_OBJECT;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }
}
