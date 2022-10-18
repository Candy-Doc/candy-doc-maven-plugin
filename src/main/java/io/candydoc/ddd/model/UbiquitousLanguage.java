package io.candydoc.ddd.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class UbiquitousLanguage {

    private final Map<DDDPattern, Set<DomainModel>> domainModelsPerPattern;

    private UbiquitousLanguage(Set<DomainModel> domainModels) {
        domainModelsPerPattern = domainModels.stream()
            .collect(Collectors.groupingBy(DomainModel::dddPattern, Collectors.toUnmodifiableSet()));
    }

    public static UbiquitousLanguage fromDomainModels(Set<DomainModel> domainModels) {
        return new UbiquitousLanguage(domainModels);
    }

}
