package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainService extends TacticalModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES =
      Set.of(
          TacticalPattern.DOMAIN_SERVICE,
          TacticalPattern.DOMAIN_EVENT,
          TacticalPattern.AGGREGATE,
          TacticalPattern.DOMAIN_COMMAND);

  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.DOMAIN_SERVICE;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }
}
