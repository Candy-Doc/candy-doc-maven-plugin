package io.candydoc.ddd.model;

import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
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

        Map<DDDPattern, Set<DomainModel>> domainModelsPerPattern = domainModels.stream()
            .collect(Collectors.groupingBy(DomainModel::dddPattern, Collectors.toUnmodifiableSet()));

        Set<DomainModel> modelsToProcess = new HashSet<>(domainModels);
        Set<UbiquitousLanguage> ubiquitousLanguages = new HashSet<>();

        domainModelsPerPattern.getOrDefault(StrategicPattern.BOUNDED_CONTEXT, Set.of()).stream()
            .map(boundedContext -> {
                Set<DomainModel> boundedDomainModels = domainModels.stream()
                    .filter(domainModel -> !boundedContext.equals(domainModel) && domainModel.packageName().startsWith(boundedContext.packageName()))
                    .collect(Collectors.toUnmodifiableSet());

                modelsToProcess.remove(boundedContext);
                modelsToProcess.removeAll(boundedDomainModels);

                return UbiquitousLanguage.BoundedVocabulary.builder()
                    .boundedContext((BoundedContext) boundedContext)
                    .domainModels(boundedDomainModels)
                    .build();
            })
            .forEach(ubiquitousLanguages::add);

        domainModelsPerPattern.getOrDefault(StrategicPattern.SHARED_KERNEL, Set.of()).stream()
            .map(sharedKernel -> {
                Set<DomainModel> sharedDomainModels = domainModels.stream()
                    .filter(domainModel -> !sharedKernel.equals(domainModel) && domainModel.packageName().startsWith(sharedKernel.packageName()))
                    .collect(Collectors.toUnmodifiableSet());

                modelsToProcess.remove(sharedKernel);
                modelsToProcess.removeAll(sharedDomainModels);

                return SharedVocabulary.builder()
                    .sharedKernel((SharedKernel) sharedKernel)
                    .domainModels(sharedDomainModels)
                    .build();
            })
            .forEach(ubiquitousLanguages::add);

        if (!modelsToProcess.isEmpty()) {
            ubiquitousLanguages.add(OrphanBoundedVocabulary.builder()
                .domainModels(modelsToProcess)
                .build());
        }

        return Set.copyOf(ubiquitousLanguages);
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
