package io.candydoc.ddd.model;

import io.candydoc.ddd.extract_domain_vocabulary.WrongUseOfDDDPatterns;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class FoundDomainObjects {

  private final Map<FullName, FoundDomainObject> domainObjects = new HashMap<>();

  public FoundDomainObjects add(FoundDomainObject domainObject) {
    domainObjects.put(domainObject.fullName(), domainObject);
    return this;
  }

  public FoundDomainObjects addAll(Collection<FoundDomainObject> domainObjects) {
    domainObjects.forEach(this::add);
    return this;
  }

  public boolean isEmpty() {
    return domainObjects.isEmpty();
  }

  public void checkCorrectUseOfDDDPatterns() {
    Set<FoundDomainObject> invalidDomainModels =
        domainObjects.values().stream()
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

    return toDomainModelBuilder(foundDDDPattern)
        .packageName(foundDomainObject.packageName())
        .fullName(foundDomainObject.fullName())
        .simpleName(foundDDDPattern.name())
        .description(foundDDDPattern.description())
        .dependencies(dependencies)
        .build();
  }

  private Set<Dependency> dependenciesOf(FoundDomainObject foundDomainObject) {
    return domainObjects.entrySet().stream()
        .filter(entry -> foundDomainObject.dependencies().contains(entry.getKey()))
        .map(
            entry ->
                Dependency.builder()
                    .name(entry.getKey())
                    .pattern(entry.getValue().matchingPattern().dddPattern())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }

  private static DomainModel.DomainModelBuilder<?, ?> toDomainModelBuilder(
      FoundDDDPattern foundDDDPattern) {
    return foundDDDPattern.accept(
        new DDDPattern.Visitor<>() {
          @Override
          public DomainModel.DomainModelBuilder<?, ?> boundedContext() {
            return BoundedContext.builder();
          }

          @Override
          public DomainModel.DomainModelBuilder<?, ?> sharedKernel() {
            return SharedKernel.builder();
          }

          @Override
          public DomainModel.DomainModelBuilder<?, ?> aggregate() {
            return Aggregate.builder();
          }

          @Override
          public DomainModel.DomainModelBuilder<?, ?> domainCommand() {
            return DomainCommand.builder();
          }

          @Override
          public DomainModel.DomainModelBuilder<?, ?> domainEvent() {
            return DomainEvent.builder();
          }

          @Override
          public DomainModel.DomainModelBuilder<?, ?> valueObject() {
            return ValueObject.builder();
          }
        });
  }
}
