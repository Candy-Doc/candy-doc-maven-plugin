package io.candydoc.plugin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.candydoc.ddd.model.DomainModel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

@Jacksonized
@Builder
@Getter
public final class DomainModelDto {

    private String type;
    private String simpleName;
    private String description;
    private Collection<DependencyDto> dependencies;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean coreConcept;

    public static DomainModelDto from(DomainModel domainModel) {
        List<DependencyDto> dependencies = new ArrayList<>();

        domainModel.allowedDependencies().stream()
            .map(dependency -> DependencyDto.builder()
                .canonicalName(dependency.name().value())
                .allowed(true)
                .build())
            .forEach(dependencies::add);

        domainModel.forbiddenDependencies().stream()
            .map(dependency -> DependencyDto.builder()
                .canonicalName(dependency.name().value())
                .allowed(false)
                .build())
            .forEach(dependencies::add);

        dependencies.sort(Comparator.comparing(DependencyDto::getCanonicalName));

        return DomainModelDto.builder()
            .type(domainModel.dddPattern().name())
            .simpleName(domainModel.simpleName().value())
            .description(domainModel.description().value())
            .dependencies(Collections.unmodifiableCollection(dependencies))
            .coreConcept(domainModel.coreConcept())
            .build();
    }
}
