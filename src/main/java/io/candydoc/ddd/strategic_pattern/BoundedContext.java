package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DDDPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Set;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BoundedContext extends StrategicModel {

    private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.copyOf(Arrays.asList(TacticalPattern.values()));

    @Override
    public DDDPattern dddPattern() {
        return StrategicPattern.BOUNDED_CONTEXT;
    }

    @Override
    public Set<DDDPattern> allowedDependencyPatterns() {
        return ALLOWED_DEPENDENCIES;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.boundedContext(this);
    }
}
