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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DDDConceptFinder {

  public static final Map<Class<? extends Annotation>, Function<Class<?>, DDDConcept>>
      ANNOTATION_PROCESSORS =
          Map.of(
              io.candydoc.ddd.annotations.BoundedContext.class,
              DDDConceptFinder::toBoundedContext,
              io.candydoc.ddd.annotations.CoreConcept.class,
              DDDConceptFinder::toCoreConcept,
              io.candydoc.ddd.annotations.ValueObject.class,
              DDDConceptFinder::toValueObject,
              io.candydoc.ddd.annotations.DomainEvent.class,
              DDDConceptFinder::toDomainEvent,
              io.candydoc.ddd.annotations.DomainCommand.class,
              DDDConceptFinder::toDomainCommand,
              io.candydoc.ddd.annotations.Aggregate.class,
              DDDConceptFinder::toAggregate);

  public abstract Set<DDDConcept> findDDDConcepts();

  public Set<Aggregate> findAggregates(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(Aggregate.class))
        .map(Aggregate.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<BoundedContext> findBoundedContexts(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(BoundedContext.class))
        .map(BoundedContext.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<CoreConcept> findCoreConcepts(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(CoreConcept.class))
        .map(CoreConcept.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<DomainCommand> findDomainCommands(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainCommand.class))
        .map(DomainCommand.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<DomainEvent> findDomainEvents(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(DomainEvent.class))
        .map(DomainEvent.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<ValueObject> findValueObjects(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(ValueObject.class))
        .map(ValueObject.class::cast)
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<Interaction> findInteractionsWith(CanonicalName conceptName) {
    return conceptsInteractingWith(conceptName).stream()
        .map(canonicalName -> Interaction.with(canonicalName.value()))
        .collect(Collectors.toUnmodifiableSet());
  }

  public DDDConcept findConcept(CanonicalName conceptName) {
    return findDDDConcepts().stream()
        .filter(concept -> concept.getCanonicalName().equals(conceptName))
        .findFirst()
        .orElseThrow();
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

  private static Set<CanonicalName> conceptsInteractingWith(CanonicalName conceptName) {
    try {
      Class<?> clazz = Class.forName(conceptName.value());
      Set<Class<?>> interactions = new HashSet<>();

      Arrays.stream(clazz.getDeclaredFields()).map(Field::getType).forEach(interactions::add);

      Arrays.stream(clazz.getDeclaredMethods())
          .forEach(
              method -> {
                interactions.add(method.getReturnType());
                interactions.addAll(Set.of(method.getParameterTypes()));
              });

      return Set.copyOf(
          interactions.stream()
              .filter(
                  interaction -> {
                    for (Annotation annotation : interaction.getAnnotations()) {
                      if (ANNOTATION_PROCESSORS.keySet().contains(annotation.annotationType())) {
                        return true;
                      }
                    }
                    return false;
                  })
              .map(interaction -> CanonicalName.of(interaction.getCanonicalName()))
              .collect(Collectors.toUnmodifiableSet()));
    } catch (ClassNotFoundException e) {
      throw new ExtractionException(e.getMessage());
    }
  }
}
