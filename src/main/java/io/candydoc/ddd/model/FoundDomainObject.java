package io.candydoc.ddd.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class FoundDomainObject {
    @Singular
    private final Set<FoundDDDPattern> matchingPatterns;
    private final PackageName packageName;
    private final CanonicalName canonicalName;
    @Singular
    private final Set<CanonicalName> dependencies;

    public FoundDDDPattern matchingPattern() {
        return matchingPatterns.iterator().next();
    }

    public boolean matchMoreThanOneDDDPattern() {
        return matchingPatterns.size() > 1;
    }
}
