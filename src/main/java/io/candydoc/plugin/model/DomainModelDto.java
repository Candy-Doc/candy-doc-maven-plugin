package io.candydoc.plugin.model;

import io.candydoc.ddd.model.AuthorizationDependenciesVisitor;
import io.candydoc.ddd.model.DomainModel;
import java.util.*;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
public final class DomainModelDto {

  private String type;
  private String simpleName;
  private String description;
  private Collection<DependencyDto> dependencies;

  public static DomainModelDto from(DomainModel domainModel) {
    List<DependencyDto> dependencies = domainModel.accept(new AuthorizationDependenciesVisitor());
    dependencies.sort(Comparator.comparing(DependencyDto::getRefersTo));

    return DomainModelDto.builder()
        .type(domainModel.dddPattern().name())
        .simpleName(domainModel.simpleName().value())
        .description(domainModel.description().value())
        .dependencies(Collections.unmodifiableCollection(dependencies))
        .build();
  }
}
