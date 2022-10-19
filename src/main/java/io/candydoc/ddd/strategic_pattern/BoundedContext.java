package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DDDPattern;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
public class BoundedContext extends StrategicPattern {

    private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of();

    @Override
    public DDDPattern dddPattern() {
        return DDDPattern.Strategic.BOUNDED_CONTEXT;
    }

    @Override
    public Set<DDDPattern> allowedDependencyPatterns() {
        return ALLOWED_DEPENDENCIES;
    }

}
