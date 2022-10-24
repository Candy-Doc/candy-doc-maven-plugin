package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.model.DomainModel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class StrategicModel extends DomainModel {

    public abstract <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        T boundedContext(BoundedContext boundedContext);
        T sharedKernel(SharedKernel sharedKernel);
    }
}
