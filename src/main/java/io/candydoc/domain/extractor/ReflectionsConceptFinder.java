package io.candydoc.domain.extractor;

import io.candydoc.domain.annotations.*;
import io.candydoc.domain.model.DDDConcept;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ReflectionsConceptFinder implements DDDConceptFinder {

  @Override
  public Set<DDDConcept> findAggregates(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(Aggregate.class).stream()
        .map(
            clazz -> {
              String annotatedName = clazz.getAnnotation(Aggregate.class).name();
              if (annotatedName.isBlank()) annotatedName = clazz.getSimpleName();
              return DDDConcept.builder()
                  .canonicalName(clazz.getCanonicalName())
                  .packageName(clazz.getPackageName())
                  .name(annotatedName)
                  .description(clazz.getAnnotation(Aggregate.class).description())
                  .build();
            })
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findBoundedContexts(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(BoundedContext.class).stream()
        .map(
            clazz ->
                DDDConcept.builder()
                    .canonicalName(clazz.getCanonicalName())
                    .packageName(clazz.getPackageName())
                    .name(clazz.getAnnotation(BoundedContext.class).name())
                    .description(clazz.getAnnotation(BoundedContext.class).description())
                    .build())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findCoreConcepts(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(CoreConcept.class).stream()
        .filter(coreConcept -> !isAnonymous(coreConcept))
        .map(
            clazz -> {
              String annotatedName = clazz.getAnnotation(CoreConcept.class).name();
              if (annotatedName.isBlank()) annotatedName = clazz.getSimpleName();
              return DDDConcept.builder()
                  .canonicalName(clazz.getCanonicalName())
                  .packageName(clazz.getPackageName())
                  .name(annotatedName)
                  .description(clazz.getAnnotation(CoreConcept.class).description())
                  .build();
            })
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findDomainCommands(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(DomainCommand.class).stream()
        .map(
            clazz ->
                DDDConcept.builder()
                    .canonicalName(clazz.getCanonicalName())
                    .packageName(clazz.getPackageName())
                    .name(clazz.getSimpleName())
                    .description(clazz.getAnnotation(DomainCommand.class).description())
                    .build())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findDomainEvents(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(DomainEvent.class).stream()
        .map(
            clazz ->
                DDDConcept.builder()
                    .canonicalName(clazz.getCanonicalName())
                    .packageName(clazz.getPackageName())
                    .name(clazz.getSimpleName())
                    .description(clazz.getAnnotation(DomainEvent.class).description())
                    .build())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findValueObjects(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(ValueObject.class).stream()
        .filter(coreConcept -> !isAnonymous(coreConcept))
        .map(
            clazz ->
                DDDConcept.builder()
                    .canonicalName(clazz.getCanonicalName())
                    .packageName(clazz.getPackageName())
                    .name(clazz.getSimpleName())
                    .description(clazz.getAnnotation(ValueObject.class).description())
                    .build())
        .collect(Collectors.toSet());
  }

  private boolean isAnonymous(Class<?> coreConcept) {
    return coreConcept.isAnonymousClass();
  }
}
