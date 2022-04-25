package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.annotations.*;
import io.candydoc.domain.model.DDDAnnotation;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDField;
import io.candydoc.domain.model.DDDMethod;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
                  .dddAnnotation(DDDAnnotation.builder().annotation(Aggregate.class).build())
                  .fields(getDeclaredFields(clazz))
                  .methods(getDeclaredMethods(clazz))
                  .parent(clazz.getSuperclass())
                  .build();
            })
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DDDConcept> findBoundedContexts(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(BoundedContext.class).stream()
        .map(
            clazz -> {
              String annotatedName = clazz.getAnnotation(BoundedContext.class).name();
              if (annotatedName.isBlank()) annotatedName = clazz.getSimpleName();
              return DDDConcept.builder()
                  .canonicalName(clazz.getCanonicalName())
                  .packageName(clazz.getPackageName())
                  .name(annotatedName)
                  .description(clazz.getAnnotation(BoundedContext.class).description())
                  .dddAnnotation(DDDAnnotation.builder().annotation(BoundedContext.class).build())
                  .fields(getDeclaredFields(clazz))
                  .methods(getDeclaredMethods(clazz))
                  .parent(clazz.getSuperclass())
                  .build();
            })
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
                  .packageName(clazz.getPackageName())
                  .name(annotatedName)
                  .canonicalName(clazz.getCanonicalName())
                  .description(clazz.getAnnotation(CoreConcept.class).description())
                  .dddAnnotation(DDDAnnotation.builder().annotation(CoreConcept.class).build())
                  .fields(getDeclaredFields(clazz))
                  .methods(getDeclaredMethods(clazz))
                  .parent(clazz.getSuperclass())
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
                    .dddAnnotation(DDDAnnotation.builder().annotation(DomainCommand.class).build())
                    .fields(getDeclaredFields(clazz))
                    .methods(getDeclaredMethods(clazz))
                    .parent(clazz.getSuperclass())
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
                    .dddAnnotation(DDDAnnotation.builder().annotation(DomainEvent.class).build())
                    .fields(getDeclaredFields(clazz))
                    .methods(getDeclaredMethods(clazz))
                    .parent(clazz.getSuperclass())
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
                    .dddAnnotation(DDDAnnotation.builder().annotation(ValueObject.class).build())
                    .fields(getDeclaredFields(clazz))
                    .methods(getDeclaredMethods(clazz))
                    .parent(clazz.getSuperclass())
                    .build())
        .collect(Collectors.toSet());
  }

  private Set<DDDField> getDeclaredFields(Class<?> currentConcept) {
    Set<DDDField> fields = new HashSet<>();
    fields.addAll(
        Arrays.stream(currentConcept.getDeclaredFields())
            .map(field -> DDDField.builder().name(field.getName()).type(field.getType()).build())
            .collect(Collectors.toSet()));
    return fields;
  }

  private Set<DDDMethod> getDeclaredMethods(Class<?> currentConcept) {
    return Arrays.stream(currentConcept.getDeclaredMethods())
        .map(
            method ->
                DDDMethod.builder()
                    .name(method.getName())
                    .parameterTypes(List.of(method.getParameterTypes()))
                    .returnType(method.getReturnType())
                    .build())
        .collect(Collectors.toSet());
  }

  private boolean isAnonymous(Class<?> coreConcept) {
    return coreConcept.isAnonymousClass();
  }
}
