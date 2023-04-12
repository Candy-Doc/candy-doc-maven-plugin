package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainCommand extends TacticalModelWithDeterredDependencies {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
      TacticalPattern.VALUE_OBJECT,
      TacticalPattern.ENTITY);

  private static final Set<DDDPattern> DETERRED_DEPENDENCIES =  Set.of(
      TacticalPattern.ENTITY);
  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.DOMAIN_COMMAND;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }

  @Override
  public Set<DDDPattern> deterredDependenciesPatterns() {
    return DETERRED_DEPENDENCIES;
  }
}
