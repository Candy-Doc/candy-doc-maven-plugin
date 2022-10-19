package io.candydoc.ddd.model;

import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface UbiquitousLanguage {

    static Set<UbiquitousLanguage> fromDomainModels(Set<DomainModel> domainModels) {
        Map<DDDPattern, Set<DomainModel>> domainModelsPerPattern = domainModels.stream()
            .collect(Collectors.groupingBy(DomainModel::dddPattern, Collectors.toUnmodifiableSet()));

        Set<UbiquitousLanguage> ubiquitousLanguages = new HashSet<>();

        domainModelsPerPattern.getOrDefault(DDDPattern.Strategic.BOUNDED_CONTEXT, Set.of()).stream()
            .map(boundedContext -> {
                Set<DomainModel> boundedDomainModels = domainModels.stream()
                    .filter(domainModel -> !boundedContext.equals(domainModel) && domainModel.packageName().startsWith(boundedContext.packageName()))
                    .collect(Collectors.toUnmodifiableSet());

                return UbiquitousLanguage.BoundedVocabulary.builder()
                    .boundedContext((BoundedContext) boundedContext)
                    .domainModels(boundedDomainModels)
                    .build();
            })
            .forEach(ubiquitousLanguages::add);

        domainModelsPerPattern.getOrDefault(DDDPattern.Strategic.SHARED_KERNEL, Set.of()).stream()
            .map(sharedKernel -> {
                Set<DomainModel> sharedDomainModels = domainModels.stream()
                    .filter(domainModel -> !sharedKernel.equals(domainModel) && domainModel.packageName().startsWith(sharedKernel.packageName()))
                    .collect(Collectors.toUnmodifiableSet());

                return SharedVocabulary.builder()
                    .sharedKernel((SharedKernel) sharedKernel)
                    .domainModels(sharedDomainModels)
                    .build();
            })
            .forEach(ubiquitousLanguages::add);

        return Set.copyOf(ubiquitousLanguages);
    }

    @Builder
    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode
    @ToString
    final class BoundedVocabulary implements UbiquitousLanguage {
        private final BoundedContext boundedContext;
        private final Set<DomainModel> domainModels;
    }

    @Builder
    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode
    @ToString
    class SharedVocabulary implements UbiquitousLanguage {
        private final SharedKernel sharedKernel;
        private final Set<DomainModel> domainModels;
    }


}
