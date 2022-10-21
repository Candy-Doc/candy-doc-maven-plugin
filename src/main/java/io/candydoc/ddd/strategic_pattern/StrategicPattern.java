package io.candydoc.ddd.strategic_pattern;

import io.candydoc.ddd.annotations.BoundedContext;
import io.candydoc.ddd.annotations.SharedKernel;
import io.candydoc.ddd.model.DDDPattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum StrategicPattern implements DDDPattern {
    BOUNDED_CONTEXT(BoundedContext.class) {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.boundedContext();
        }
    },
    SHARED_KERNEL(SharedKernel.class) {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.sharedKernel();
        }
    };

    private final Class<? extends Annotation> relatedAnnotation;
}
