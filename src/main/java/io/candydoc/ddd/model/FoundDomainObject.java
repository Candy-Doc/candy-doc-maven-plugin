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
    private Set<FoundDDDPattern> matchingPatterns;
    private PackageName packageName;
    private CanonicalName canonicalName;
    @Singular
    private Set<CanonicalName> dependencies;

    public FoundDDDPattern matchingPattern() {
        return matchingPatterns.iterator().next();
    }

    // fixme: test when it is annotated with more than one pattern
    public boolean matchMoreThanOneDDDPattern() {
        return matchingPatterns.size() > 1;
    }
}
