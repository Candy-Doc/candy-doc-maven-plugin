package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.rules.DomainEntityId;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainEntity extends TacticalModel {

  private static final Set<DDDPattern> ALLOWED_DEPENDENCIES =
      Set.of(TacticalPattern.DOMAIN_ENTITY, TacticalPattern.VALUE_OBJECT);

  @Singular
  @Getter(AccessLevel.PUBLIC)
  private Set<DomainEntityId> domainEntityIds;

  @Override
  public DDDPattern dddPattern() {
    return TacticalPattern.DOMAIN_ENTITY;
  }

  @Override
  public Set<DDDPattern> allowedDependencyPatterns() {
    return ALLOWED_DEPENDENCIES;
  }
}
