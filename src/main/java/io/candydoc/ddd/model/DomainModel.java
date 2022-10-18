package io.candydoc.ddd.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@SuperBuilder
@Accessors(fluent = true)
@Getter
@ToString
public abstract class DomainModel {
    private final CanonicalName canonicalName;
    private final SimpleName simpleName;
    private final PackageName packageName;
    private final Description description;
    private final Set<Dependency> dependencies;

    public Set<Dependency> allowedDependencies() {
        return dependencies.stream()
            .filter(dependency -> allowedDependencyPatterns().contains(dependency.pattern()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Dependency> forbiddenDependencies() {
        return dependencies.stream()
            .filter(dependency -> !allowedDependencyPatterns().contains(dependency.pattern()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public abstract DDDPattern dddPattern();

    public abstract Set<DDDPattern> allowedDependencyPatterns();
}
