package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.annotations.BoundedContext;
import io.candydoc.ddd.annotations.SharedKernel;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.model.DomainWarning;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;

@Slf4j
public class ReflectionsDomainVocabularyFinder implements DomainVocabularyFinder {

  public static final Map<DDDPattern, Function<Class<?>, FoundDDDPattern>> DDD_PATTERN_PROCESSOR =
      Map.of(
          StrategicPattern.BOUNDED_CONTEXT,
          ReflectionsDomainVocabularyFinder::toBoundedContextPattern,
          StrategicPattern.SHARED_KERNEL,
          ReflectionsDomainVocabularyFinder::toSharedKernel,
          TacticalPattern.AGGREGATE,
          ReflectionsDomainVocabularyFinder::toAggregatePattern,
          TacticalPattern.DOMAIN_COMMAND,
          ReflectionsDomainVocabularyFinder::toDomainCommandPattern,
          TacticalPattern.DOMAIN_EVENT,
          ReflectionsDomainVocabularyFinder::toDomainEventPattern,
          TacticalPattern.VALUE_OBJECT,
          ReflectionsDomainVocabularyFinder::toValueObjectPattern,
          TacticalPattern.DOMAIN_ENTITY,
          ReflectionsDomainVocabularyFinder::toEntityPattern,
          TacticalPattern.FACTORY,
          ReflectionsDomainVocabularyFinder::toFactoryPattern,
          TacticalPattern.REPOSITORY,
          ReflectionsDomainVocabularyFinder::toRepositoryPattern,
          TacticalPattern.DOMAIN_SERVICE,
          ReflectionsDomainVocabularyFinder::toDomainServicePattern);

  public static final Map<DDDPattern, Function<Class<?>, Set<DomainWarning>>>
      DDD_WARNING_PROCESSOR =
          Map.of(
              StrategicPattern.BOUNDED_CONTEXT,
              ReflectionsDomainVocabularyFinder::checkBoundedContextRules,
              StrategicPattern.SHARED_KERNEL,
              ReflectionsDomainVocabularyFinder::checkSharedKernelRules,
              TacticalPattern.AGGREGATE,
              ReflectionsDomainVocabularyFinder::checkAggregateRules,
              TacticalPattern.DOMAIN_COMMAND,
              ReflectionsDomainVocabularyFinder::checkDomainCommandRules,
              TacticalPattern.DOMAIN_EVENT,
              ReflectionsDomainVocabularyFinder::checkDomainEventRules,
              TacticalPattern.VALUE_OBJECT,
              ReflectionsDomainVocabularyFinder::checkValueObjectRules,
              TacticalPattern.DOMAIN_ENTITY,
              ReflectionsDomainVocabularyFinder::checkEntityRules,
              TacticalPattern.FACTORY,
              ReflectionsDomainVocabularyFinder::checkFactoryRules,
              TacticalPattern.REPOSITORY,
              ReflectionsDomainVocabularyFinder::checkRepositoryRules,
              TacticalPattern.DOMAIN_SERVICE,
              ReflectionsDomainVocabularyFinder::checkDomainServiceRules);

  @Override
  public FoundDomainObjects findDomainObjects(Collection<String> packagesToScan) {
    String[] packagesAsArray = packagesToScan.toArray(new String[] {});
    final ConfigurationBuilder config =
        new ConfigurationBuilder()
            .setScanners(new SubTypesScanner(false))
            .forPackages(packagesAsArray)
            .filterInputsBy(new FilterBuilder().includePackage(packagesAsArray));

    Reflections reflections = new Reflections(config);

    FoundDomainObjects foundDomainObjects = new FoundDomainObjects();
    Set<Class<?>> scannedClasses = reflections.getSubTypesOf(Object.class);

    scannedClasses.forEach(
        scannedClass -> {
          Set<Class<? extends Annotation>> matchingAnnotations =
              DDD_PATTERN_PROCESSOR.keySet().stream()
                  .map(DDDPattern::relatedAnnotation)
                  .filter(scannedClass::isAnnotationPresent)
                  .collect(Collectors.toUnmodifiableSet());

          if (!matchingAnnotations.isEmpty()) {
            foundDomainObjects.add(
                FoundDomainObject.builder()
                    .matchingPatterns(toFoundDDDPatterns(matchingAnnotations, scannedClass))
                    .packageName(PackageName.of(scannedClass.getPackageName()))
                    .canonicalName(CanonicalName.of(scannedClass.getCanonicalName()))
                    .dependencies(dependenciesOf(scannedClass, scannedClasses))
                    .warnings(checkPatternsRules(matchingAnnotations, scannedClass))
                    .build());
          }
        });

    return foundDomainObjects;
  }

  private static Set<FoundDDDPattern> toFoundDDDPatterns(
      Set<Class<? extends Annotation>> matchingAnnotations, Class<?> clazz) {
    return DDD_PATTERN_PROCESSOR.keySet().stream()
        .filter(dddPattern -> matchingAnnotations.contains(dddPattern.relatedAnnotation()))
        .map(dddPattern -> toFoundDDDPattern(dddPattern, clazz))
        .collect(Collectors.toUnmodifiableSet());
  }

  private static FoundDDDPattern toFoundDDDPattern(DDDPattern dddPattern, Class<?> clazz) {
    return DDD_PATTERN_PROCESSOR.get(dddPattern).apply(clazz);
  }

  private static FoundDDDPattern toBoundedContextPattern(Class<?> clazz) {
    String simpleName =
        clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).name();
    if (simpleName.isBlank()) {
      String packageName = clazz.getPackageName();
      String[] packageParts = packageName.split("\\.");
      simpleName = packageParts[packageParts.length - 1];
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).description();
    return toFoundDDDPattern(StrategicPattern.BOUNDED_CONTEXT, simpleName, description);
  }

  private static FoundDDDPattern toSharedKernel(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).name();
    if (simpleName.isBlank()) {
      String packageName = clazz.getPackageName();
      String[] packageParts = packageName.split("\\.");
      simpleName = packageParts[packageParts.length - 1];
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).description();
    return toFoundDDDPattern(StrategicPattern.SHARED_KERNEL, simpleName, description);
  }

  private static FoundDDDPattern toAggregatePattern(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).description();
    return toFoundDDDPattern(TacticalPattern.AGGREGATE, simpleName, description);
  }

  private static FoundDDDPattern toDomainCommandPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();
    return toFoundDDDPattern(TacticalPattern.DOMAIN_COMMAND, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toDomainEventPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();
    return toFoundDDDPattern(TacticalPattern.DOMAIN_EVENT, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toFactoryPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.Factory.class).description();
    return toFoundDDDPattern(TacticalPattern.FACTORY, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toRepositoryPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.Repository.class).description();
    return toFoundDDDPattern(TacticalPattern.REPOSITORY, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toDomainServicePattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainService.class).description();
    return toFoundDDDPattern(TacticalPattern.DOMAIN_SERVICE, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toValueObjectPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();
    return toFoundDDDPattern(TacticalPattern.VALUE_OBJECT, clazz.getSimpleName(), description);
  }

  private static FoundDDDPattern toEntityPattern(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEntity.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEntity.class).description();
    return toFoundDDDPattern(TacticalPattern.DOMAIN_ENTITY, simpleName, description);
  }

  private static FoundDDDPattern toFoundDDDPattern(
      DDDPattern dddPattern, String simpleName, String description) {
    return FoundDDDPattern.builder()
        .dddPattern(dddPattern)
        .name(SimpleName.of(simpleName))
        .description(Description.of(description))
        .build();
  }

  private static Set<CanonicalName> dependenciesOf(
      Class<?> domainModel, Set<Class<?>> scannedClasses) {
    if (domainModel.isAnnotationPresent(BoundedContext.class)
        || domainModel.isAnnotationPresent(SharedKernel.class)) {
      String packageName = domainModel.getPackageName();
      return scannedClasses.stream()
          .filter(
              clazz -> !clazz.equals(domainModel) && clazz.getPackageName().startsWith(packageName))
          .map(dependency -> CanonicalName.of(dependency.getCanonicalName()))
          .collect(Collectors.toUnmodifiableSet());
    }
    return dependenciesOfTacticalPattern(domainModel);
  }

  private static List<Class<?>> getFieldType(Field field) {
    Type fieldType = field.getGenericType();
    if (fieldType instanceof ParameterizedType) {
      List<Class<?>> actualArgumentClasses = new ArrayList<>();
      Type[] actualTypeArguments = ((ParameterizedType) fieldType).getActualTypeArguments();
      for (Type type : actualTypeArguments) {
        if (type instanceof Class) {
          actualArgumentClasses.add((Class<?>) type);
        }
      }
      return actualArgumentClasses;
    }
    return List.of(field.getType());
  }

  private static Set<CanonicalName> dependenciesOfTacticalPattern(Class<?> domainModel) {
    Set<Class<?>> dependencies = new HashSet<>();

    Arrays.stream(domainModel.getDeclaredFields())
        .map(ReflectionsDomainVocabularyFinder::getFieldType)
        .forEach(dependencies::addAll);

    Arrays.stream(domainModel.getDeclaredMethods())
        .forEach(
            method -> {
              dependencies.add(method.getReturnType());
              dependencies.addAll(Arrays.asList(method.getParameterTypes()));
            });

    return dependencies.stream()
        .map(dependency -> CanonicalName.of(dependency.getCanonicalName()))
        .collect(Collectors.toUnmodifiableSet());
  }

  private static Set<DomainWarning> checkPatternsRules(
      Set<Class<? extends Annotation>> matchingAnnotations, Class<?> clazz) {
    return DDD_WARNING_PROCESSOR.keySet().stream()
        .filter(dddPattern -> matchingAnnotations.contains(dddPattern.relatedAnnotation()))
        .map(dddPattern -> checkPatternRules(dddPattern, clazz))
        .iterator()
        .next();
  }

  private static Set<DomainWarning> checkPatternRules(DDDPattern dddPattern, Class<?> clazz) {
    return DDD_WARNING_PROCESSOR.get(dddPattern).apply(clazz);
  }

  private static Set<DomainWarning> checkBoundedContextRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkSharedKernelRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkAggregateRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainCommandRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainEventRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkValueObjectRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkFactoryRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkRepositoryRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkDomainServiceRules(Class<?> clazz) {
    return new HashSet<>();
  }

  private static Set<DomainWarning> checkEntityRules(Class<?> clazz) {
    Set<DomainWarning> warnings = new HashSet<>();
    Set<Field> domainEntityIds =
        Arrays.stream(clazz.getDeclaredFields())
            .filter(
                field ->
                    field.isAnnotationPresent(io.candydoc.ddd.annotations.DomainEntityId.class))
            .collect(Collectors.toUnmodifiableSet());

    if (domainEntityIds.isEmpty()) {
      warnings.add(DomainWarning.NO_ID);
    }
    return warnings;
  }
}
