package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DDDPattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SharedKernel extends StrategicPattern {

    private static final Set<DDDPattern> ALLOWED_DEPENDENCIES = Set.of();

    @Override
    public DDDPattern dddPattern() {
        return DDDPattern.Strategic.SHARED_KERNEL;
    }

    @Override
    public Set<DDDPattern> allowedDependencyPatterns() {
        return ALLOWED_DEPENDENCIES;
    }
}
