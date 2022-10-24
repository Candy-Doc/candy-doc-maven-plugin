package io.candydoc.ddd.model;

import io.candydoc.ddd.extract_domain_vocabulary.NestedStrategicPatternsForbidden;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.strategic_pattern.StrategicModel;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface UbiquitousLanguage {

    Collection<DomainModel> domainModels();

    static Set<UbiquitousLanguage> fromDomainObjects(FoundDomainObjects foundDomainObjects) {
        foundDomainObjects.checkConsistency();

        Set<DomainModel> domainModels = foundDomainObjects.toDomainModels();
        Set<DomainModel> modelsToProcess = new HashSet<>(domainModels);
        Set<UbiquitousLanguage> ubiquitousLanguages = new HashSet<>();

        domainModels.stream()
            .filter(StrategicModel.class::isInstance)
            .map(StrategicModel.class::cast)
            .forEach(strategicModel -> {
                if (!strategicModel.forbiddenDependencies().isEmpty()) {
                    throw new NestedStrategicPatternsForbidden(strategicModel);
                }

                Set<DomainModel> boundedModels = domainModels.stream()
                    .filter(domainModel -> strategicModel.dependsOn(domainModel.canonicalName()))
                    .collect(Collectors.toUnmodifiableSet());

                modelsToProcess.remove(strategicModel);
                modelsToProcess.removeAll(boundedModels);

                UbiquitousLanguage ubiquitousLanguage = toUbiquitousLanguage(strategicModel, boundedModels);
                ubiquitousLanguages.add(ubiquitousLanguage);
            });

        if (!modelsToProcess.isEmpty()) {
            ubiquitousLanguages.add(OrphanBoundedVocabulary.builder()
                .domainModels(modelsToProcess)
                .build());
        }

        return Set.copyOf(ubiquitousLanguages);
    }

    static UbiquitousLanguage toUbiquitousLanguage(StrategicModel strategicModel, Set<DomainModel> boundedModels) {
        return strategicModel.accept(new StrategicModel.Visitor<>() {
            @Override
            public UbiquitousLanguage boundedContext(BoundedContext boundedContext) {
                return UbiquitousLanguage.BoundedVocabulary.builder()
                    .boundedContext((BoundedContext) strategicModel)
                    .domainModels(boundedModels)
                    .build();
            }

            @Override
            public UbiquitousLanguage sharedKernel(SharedKernel sharedKernel) {
                return SharedVocabulary.builder()
                    .sharedKernel((SharedKernel) strategicModel)
                    .domainModels(boundedModels)
                    .build();
            }
        });
    }

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T handle(BoundedVocabulary vocabulary);

        T handle(SharedVocabulary vocabulary);

        T handle(OrphanBoundedVocabulary vocabulary);
    }

    @Builder
    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode
    @ToString
    final class BoundedVocabulary implements UbiquitousLanguage {
        private final BoundedContext boundedContext;
        private final Collection<DomainModel> domainModels;

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.handle(this);
        }
    }

    @Builder
    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode
    @ToString
    class SharedVocabulary implements UbiquitousLanguage {
        private final SharedKernel sharedKernel;
        private final Collection<DomainModel> domainModels;

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.handle(this);
        }
    }

    @Builder
    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode
    @ToString
    final class OrphanBoundedVocabulary implements UbiquitousLanguage {
        private final Collection<DomainModel> domainModels;

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.handle(this);
        }
    }


}
