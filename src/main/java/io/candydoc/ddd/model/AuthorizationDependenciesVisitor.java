package io.candydoc.ddd.model;

import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.*;
import io.candydoc.plugin.model.DependencyAuthorization;
import io.candydoc.plugin.model.DependencyDto;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationDependenciesVisitor implements DependencyVisitor {

  private void addAllowedDependencies(DomainModel domainModel, List<DependencyDto> dependencies) {
    domainModel.allowedDependencies().stream()
        .map(
            dependency ->
                DependencyDto.builder()
                    .refersTo(dependency.name().value())
                    .authorization(DependencyAuthorization.ALLOWED)
                    .build())
        .forEach(dependencies::add);
  }

  private void addForbiddenDependencies(DomainModel domainModel, List<DependencyDto> dependencies) {
    domainModel.forbiddenDependencies().stream()
        .map(
            dependency ->
                DependencyDto.builder()
                    .refersTo(dependency.name().value())
                    .authorization(DependencyAuthorization.FORBIDDEN)
                    .build())
        .forEach(dependencies::add);
  }

  private void addForbiddenDependencies(
      TacticalModelWithDeterredDependencies domainModel, List<DependencyDto> dependencies) {
    domainModel.forbiddenDependencies().stream()
        .map(
            dependency ->
                DependencyDto.builder()
                    .refersTo(dependency.name().value())
                    .authorization(DependencyAuthorization.FORBIDDEN)
                    .build())
        .forEach(dependencies::add);
  }

  private void addDeterredDependencies(
      TacticalModelWithDeterredDependencies model, List<DependencyDto> dependencies) {
    model.deterredDependencies().stream()
        .map(
            dependency ->
                DependencyDto.builder()
                    .refersTo(dependency.name().value())
                    .authorization(DependencyAuthorization.DETERRED)
                    .build())
        .forEach(dependencies::add);
  }

  private void addAllDependencies(DomainModel domainModel, List<DependencyDto> dependencies) {
    addAllowedDependencies(domainModel, dependencies);
    addForbiddenDependencies(domainModel, dependencies);
  }

  private void addAllDependenciesWithDeterred(
      TacticalModelWithDeterredDependencies domainModel, List<DependencyDto> dependencies) {
    addAllowedDependencies(domainModel, dependencies);
    addDeterredDependencies(domainModel, dependencies);
    addForbiddenDependencies(domainModel, dependencies);
  }

  @Override
  public List<DependencyDto> visit(BoundedContext boundedContext) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependencies(boundedContext, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(SharedKernel sharedKernel) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependencies(sharedKernel, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(Aggregate aggregate) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependencies(aggregate, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(DomainEvent domainEvent) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependenciesWithDeterred(domainEvent, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(DomainCommand domainCommand) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependenciesWithDeterred(domainCommand, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(Entity entity) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependencies(entity, dependencies);
    return dependencies;
  }

  @Override
  public List<DependencyDto> visit(ValueObject valueObject) {
    List<DependencyDto> dependencies = new ArrayList<>();
    addAllDependencies(valueObject, dependencies);
    return dependencies;
  }
}
