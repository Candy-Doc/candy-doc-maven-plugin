package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.DDDPattern;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
public class BoundedContext extends DomainModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
      DDDPattern.Tactical.AGGREGATE,
      DDDPattern.Tactical.DOMAIN_COMMAND,
      DDDPattern.Tactical.DOMAIN_EVENT,
      DDDPattern.Tactical.VALUE_OBJECT
  );

  @Override
  public DDDPattern dddPattern() {
    return DDDPattern.Strategic.BOUNDED_CONTEXT;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

}
