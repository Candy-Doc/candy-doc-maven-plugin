package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainEvent extends TacticalPattern {

    private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
        DDDPattern.Tactical.VALUE_OBJECT
    );

    @Override
    public DDDPattern dddPattern() {
        return DDDPattern.Tactical.DOMAIN_EVENT;
    }

    @Override
    public Set<DDDPattern> allowedDependencyPatterns() {
        return ALLOWED_DEPENDENCIES;
    }
}
