package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.value_object.ValueObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ReflectionsConceptFinder implements DDDConceptFinder {

  private static final Map<Class<? extends Annotation>, Function<Class<?>, DDDConcept>>
      ANNOTATION_PROCESSORS =
          Map.of(
              io.candydoc.ddd.annotations.BoundedContext.class,
                  ReflectionsConceptFinder::toBoundedContext,
              io.candydoc.ddd.annotations.CoreConcept.class,
                  ReflectionsConceptFinder::toCoreConcept,
              io.candydoc.ddd.annotations.ValueObject.class,
                  ReflectionsConceptFinder::toValueObject,
              io.candydoc.ddd.annotations.DomainEvent.class,
                  ReflectionsConceptFinder::toDomainEvent,
              io.candydoc.ddd.annotations.DomainCommand.class,
                  ReflectionsConceptFinder::toDomainCommand,
              io.candydoc.ddd.annotations.Aggregate.class, ReflectionsConceptFinder::toAggregate);

  @Override
  public Set<Aggregate> findAggregates(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(Aggregate.class))
        .map(Aggregate.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<BoundedContext> findBoundedContexts(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(BoundedContext.class))
        .map(BoundedContext.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<CoreConcept> findCoreConcepts(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(CoreConcept.class))
        .map(CoreConcept.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<DomainCommand> findDomainCommands(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainCommand.class))
        .map(DomainCommand.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<DomainEvent> findDomainEvents(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainEvent.class))
        .map(DomainEvent.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<ValueObject> findValueObjects(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(ValueObject.class))
        .map(ValueObject.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<Interaction> findInteractionsWith(CanonicalName conceptName) {
    return classesInteractingWith(conceptName).stream()
        .map(interaction -> Interaction.with(interaction.getCanonicalName()))
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public DDDConcept findConcept(CanonicalName conceptName) {
    return findDDDConcepts().stream()
        .filter(concept -> concept.getCanonicalName().equals(conceptName))
        .findFirst()
        .orElseThrow();
  }

  private Set<DDDConcept> findDDDConcepts() {
    Reflections reflections = new Reflections();

    return Set.of(
            io.candydoc.ddd.annotations.BoundedContext.class,
            io.candydoc.ddd.annotations.CoreConcept.class,
            io.candydoc.ddd.annotations.ValueObject.class,
            io.candydoc.ddd.annotations.DomainEvent.class,
            io.candydoc.ddd.annotations.DomainCommand.class,
            io.candydoc.ddd.annotations.Aggregate.class)
        .stream()
        .map(reflections::getTypesAnnotatedWith)
        .flatMap(Collection::stream)
        .map(ReflectionsConceptFinder::toDDDConcept)
        .collect(Collectors.toUnmodifiableSet());
  }

  private static DDDConcept toDDDConcept(Class<?> clazz) {
    return DDD_ANNOTATION_CLASSES.stream()
        .map(ANNOTATION_PROCESSORS::get)
        .findFirst()
        .map(processor -> processor.apply(clazz))
        .orElseThrow(); // todo tester cas d'erreur ?
  }

  private static Aggregate toAggregate(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).description();

    return Aggregate.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(simpleName))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static BoundedContext toBoundedContext(Class<?> clazz) {
    String simpleName =
        clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).description();

    return BoundedContext.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(simpleName))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static CoreConcept toCoreConcept(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.CoreConcept.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.CoreConcept.class).description();

    return CoreConcept.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(simpleName))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static DomainCommand toDomainCommand(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();

    return DomainCommand.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static DomainEvent toDomainEvent(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();

    return DomainEvent.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static ValueObject toValueObject(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();

    return ValueObject.builder()
        .canonicalName(CanonicalName.of(clazz.getCanonicalName()))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(PackageName.of(clazz.getPackageName()))
        .description(Description.of(description))
        .build();
  }

  private static Set<Class<?>> classesInteractingWith(CanonicalName conceptName) {
    try {
      Class<?> clazz = Class.forName(conceptName.value());
      Set<Class<?>> interactions = new HashSet<>();

      Arrays.stream(clazz.getDeclaredFields()).map(Field::getClass).forEach(interactions::add);

      Arrays.stream(clazz.getDeclaredMethods())
          .forEach(
              method -> {
                interactions.add(method.getReturnType());
                interactions.addAll(Set.of(method.getParameterTypes()));
              });

      return Set.copyOf(interactions);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
