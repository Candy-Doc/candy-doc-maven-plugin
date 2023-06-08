package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.annotations.Aggregate;
import io.candydoc.ddd.annotations.BoundedContext;
import io.candydoc.ddd.annotations.DomainCommand;
import io.candydoc.ddd.annotations.DomainEvent;
import io.candydoc.ddd.annotations.SharedKernel;
import io.candydoc.ddd.annotations.ValueObject;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.StrategicPattern;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;
import org.reflections8.util.ConfigurationBuilder;

@Slf4j
public class ReflectionsDomainVocabularyFinder implements DomainVocabularyFinder {

  public static final Class<? extends Annotation>[] DDD_ANNOTATIONS =
      new Class[] {
        Aggregate.class,
        BoundedContext.class,
        SharedKernel.class,
        ValueObject.class,
        DomainEvent.class,
        DomainCommand.class,
      };

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
          ReflectionsDomainVocabularyFinder::toValueObjectPattern);

  @Override
  public FoundDomainObjects findDomainObjects(Collection<String> packagesToScan) {
    //    String[] packagesAsArray = packagesToScan.toArray(new String[] {});
    //    final ConfigurationBuilder config =
    //        new ConfigurationBuilder()
    //            .setScanners(new SubTypesScanner(false))
    //            .forPackages(packagesAsArray)
    //            .filterInputsBy(new FilterBuilder().includePackage(packagesAsArray));
    //
    //    Reflections reflections = new Reflections(config);

    Reflections reflections =
        reflections =
            new Reflections(
                new ConfigurationBuilder()
                    .setUrls(
                        packagesToScan.stream()
                            .map(
                                packageToScan ->
                                    ClassLoader.getSystemClassLoader()
                                        .getResource(packageToScan.replace('.', '/')))
                            .collect(Collectors.toUnmodifiableSet())));

    FoundDomainObjects foundDomainObjects = new FoundDomainObjects();
    Set<Class<?>> scannedClasses =
        Arrays.stream(DDD_ANNOTATIONS)
            .map(reflections::getTypesAnnotatedWith)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());

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
                    .fullName(FullName.of(scannedClass.getName()))
                    .dependencies(dependenciesOf(scannedClass, scannedClasses))
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
      simpleName = lastPartOfClassName(clazz);
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).description();
    return toFoundDDDPattern(TacticalPattern.AGGREGATE, simpleName, description);
  }

  private static FoundDDDPattern toDomainCommandPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();
    return toFoundDDDPattern(
        TacticalPattern.DOMAIN_COMMAND, lastPartOfClassName(clazz), description);
  }

  private static FoundDDDPattern toDomainEventPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();
    return toFoundDDDPattern(TacticalPattern.DOMAIN_EVENT, lastPartOfClassName(clazz), description);
  }

  private static FoundDDDPattern toValueObjectPattern(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();
    return toFoundDDDPattern(TacticalPattern.VALUE_OBJECT, lastPartOfClassName(clazz), description);
  }

  private static FoundDDDPattern toFoundDDDPattern(
      DDDPattern dddPattern, String simpleName, String description) {
    return FoundDDDPattern.builder()
        .dddPattern(dddPattern)
        .name(SimpleName.of(simpleName))
        .description(Description.of(description))
        .build();
  }

  private static Set<FullName> dependenciesOf(Class<?> domainModel, Set<Class<?>> scannedClasses) {
    if (domainModel.isAnnotationPresent(BoundedContext.class)
        || domainModel.isAnnotationPresent(SharedKernel.class)) {
      String packageName = domainModel.getPackageName();
      return scannedClasses.stream()
          .filter(
              clazz -> !clazz.equals(domainModel) && clazz.getPackageName().startsWith(packageName))
          .map(dependency -> FullName.of(dependency.getName()))
          .collect(Collectors.toUnmodifiableSet());
    }
    return dependenciesOfTacticalPattern(domainModel);
  }

  private static Set<FullName> dependenciesOfTacticalPattern(Class<?> domainModel) {
    Set<Class<?>> dependencies = new HashSet<>();

    Arrays.stream(domainModel.getDeclaredFields()).map(Field::getType).forEach(dependencies::add);

    Arrays.stream(domainModel.getDeclaredMethods())
        .forEach(
            method -> {
              dependencies.add(method.getReturnType());
              dependencies.addAll(Arrays.asList(method.getParameterTypes()));
            });

    return dependencies.stream()
        .map(dependency -> FullName.of(dependency.getName()))
        .collect(Collectors.toUnmodifiableSet());
  }

  private static String lastPartOfClassName(Class<?> className) {
    String name = className.getName();
    return name.substring(name.lastIndexOf(".") + 1);
  }
}
