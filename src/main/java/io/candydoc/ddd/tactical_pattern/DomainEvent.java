package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainEvent extends TacticalModel {

    private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of(
        TacticalPattern.VALUE_OBJECT
    );

    @Override
    public DDDPattern dddPattern() {
        return TacticalPattern.DOMAIN_EVENT;
    }

    @Override
    public Set<DDDPattern> allowedDependencyPatterns() {
        return ALLOWED_DEPENDENCIES;
    }
}
