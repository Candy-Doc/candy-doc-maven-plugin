package io.candydoc.ddd.model;

import io.candydoc.ddd.extract_domain_vocabulary.WrongUseOfDDDPatterns;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public final class FoundDomainObjects {

    private final Map<CanonicalName, FoundDomainObject> domainObjects = new HashMap<>();

    public void add(FoundDomainObject domainObject) {
        domainObjects.put(domainObject.canonicalName(), domainObject);
    }

    public boolean isEmpty() {
        return domainObjects.isEmpty();
    }

    public void checkConsistency() {
        Set<FoundDomainObject> invalidDomainModels = domainObjects.values().stream()
                .filter(FoundDomainObject::matchMoreThanOneDDDPattern)
                .collect(Collectors.toUnmodifiableSet());

        if (!invalidDomainModels.isEmpty()) {
            throw new WrongUseOfDDDPatterns(invalidDomainModels);
        }
    }

    public Set<DomainModel> toDomainModels() {
        return domainObjects.values().stream()
                .map(this::toDomainModel)
                .collect(Collectors.toUnmodifiableSet());
    }

    private DomainModel toDomainModel(FoundDomainObject foundDomainObject) {
        FoundDDDPattern foundDDDPattern = foundDomainObject.matchingPattern();
        Set<Dependency> dependencies = dependenciesOf(foundDomainObject);

        return foundDDDPattern.accept(new DDDPattern.Visitor<>() {
            @Override
            public DomainModel boundedContext() {
                return BoundedContext.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }

            @Override
            public DomainModel sharedKernel() {
                return SharedKernel.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }

            @Override
            public DomainModel aggregate() {
                return Aggregate.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }

            @Override
            public DomainModel domainCommand() {
                return DomainCommand.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }

            @Override
            public DomainModel domainEvent() {
                return DomainEvent.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }

            @Override
            public DomainModel valueObject() {
                return ValueObject.builder()
                        .packageName(foundDomainObject.packageName())
                        .canonicalName(foundDomainObject.canonicalName())
                        .simpleName(foundDDDPattern.name())
                        .description(foundDDDPattern.description())
                        .dependencies(dependencies)
                        .build();
            }
        });
    }

    private Set<Dependency> dependenciesOf(FoundDomainObject foundDomainObject) {
        return domainObjects.entrySet().stream()
                .filter(entry -> foundDomainObject.dependencies().contains(entry.getKey()))
                .map(entry -> Dependency.builder()
                        .name(entry.getKey())
                        .pattern(entry.getValue().matchingPattern().dddPattern())
                        .build())
                .collect(Collectors.toUnmodifiableSet());
    }

}
