package io.candydoc.ddd.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class FoundDDDPattern {
    private DDDPattern dddPattern;
    private SimpleName name;
    private Description description;

    public <T> T accept(DDDPattern.Visitor<T> visitor) {
        return dddPattern.accept(visitor);
    }
}
