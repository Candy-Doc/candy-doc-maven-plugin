package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.shared_kernel.SharedKernel;
import io.candydoc.ddd.value_object.ValueObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ReflectionsConceptFinder implements DDDConceptFinder {

  public static final Map<Class<? extends Annotation>, Function<Class<?>, DDDConcept>>
      ANNOTATION_PROCESSORS =
          Map.of(
              io.candydoc.ddd.annotations.Aggregate.class,
              ReflectionsConceptFinder::toAggregate,
              io.candydoc.ddd.annotations.BoundedContext.class,
              ReflectionsConceptFinder::toBoundedContext,
              io.candydoc.ddd.annotations.CoreConcept.class,
              ReflectionsConceptFinder::toCoreConcept,
              io.candydoc.ddd.annotations.DomainCommand.class,
              ReflectionsConceptFinder::toDomainCommand,
              io.candydoc.ddd.annotations.DomainEvent.class,
              ReflectionsConceptFinder::toDomainEvent,
              io.candydoc.ddd.annotations.SharedKernel.class,
              ReflectionsConceptFinder::toSharedKernel,
              io.candydoc.ddd.annotations.ValueObject.class,
              ReflectionsConceptFinder::toValueObject);

  private static Set<DDDConcept> foundConcepts;

  @Override
  public Set<DDDConcept> findDDDConcepts() {
    if (foundConcepts == null) {
      Reflections reflections = new Reflections();

      foundConcepts =
          DDDKeywords.KEYWORDS.stream()
              .flatMap(
                  annotation -> {
                    Function<Class<?>, DDDConcept> processor =
                        ANNOTATION_PROCESSORS.get(annotation);

                    return reflections.getTypesAnnotatedWith(annotation).stream()
                        .filter(clazz -> !clazz.isAnonymousClass())
                        .map(processor);
                  })
              .collect(Collectors.toUnmodifiableSet());
    }
    return foundConcepts;
  }

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
        .collect(Collectors.toSet());
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
  public Set<SharedKernel> findSharedKernels(String packageToScan) {
    return findDDDConcepts().stream()
        .filter(dddConcept -> dddConcept.getPackageName().startsWith(packageToScan))
        .filter(dddConcept -> dddConcept.getClass().isAssignableFrom(SharedKernel.class))
        .map(SharedKernel.class::cast)
        .collect(Collectors.toSet());
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
    return conceptsInteractingWith(conceptName).stream()
        .map(canonicalName -> Interaction.with(canonicalName.value()))
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
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
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(simpleName))
        .packageName(toPackageName(clazz))
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
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(simpleName))
        .packageName(toPackageName(clazz))
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
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(simpleName))
        .packageName(toPackageName(clazz))
        .description(Description.of(description))
        .build();
  }

  private static DomainCommand toDomainCommand(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();

    return DomainCommand.builder()
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(toPackageName(clazz))
        .description(Description.of(description))
        .build();
  }

  private static DomainEvent toDomainEvent(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();

    return DomainEvent.builder()
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(toPackageName(clazz))
        .description(Description.of(description))
        .build();
  }

  private static DDDConcept toSharedKernel(Class<?> clazz) {
    String simpleName = clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).name();
    if (simpleName.isBlank()) {
      simpleName = clazz.getSimpleName();
    }

    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.SharedKernel.class).description();

    return SharedKernel.builder()
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(simpleName))
        .packageName(toPackageName(clazz))
        .description(Description.of(description))
        .build();
  }

  private static ValueObject toValueObject(Class<?> clazz) {
    String description =
        clazz.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();

    return ValueObject.builder()
        .canonicalName(toCanonicalName(clazz))
        .simpleName(SimpleName.of(clazz.getSimpleName()))
        .packageName(toPackageName(clazz))
        .description(Description.of(description))
        .build();
  }

  private static CanonicalName toCanonicalName(Class<?> clazz) {
    return CanonicalName.of(clazz.getCanonicalName());
  }

  private static PackageName toPackageName(Class<?> clazz) {
    return PackageName.of(clazz.getPackageName());
  }

  private static Set<CanonicalName> conceptsInteractingWith(CanonicalName conceptName) {
    try {
      Class<?> clazz =
          Class.forName(conceptName.value(), false, Thread.currentThread().getContextClassLoader());
      Set<Class<?>> interactions = new HashSet<>();

      Arrays.stream(clazz.getDeclaredFields()).map(Field::getType).forEach(interactions::add);

      Arrays.stream(clazz.getDeclaredMethods())
          .forEach(
              method -> {
                interactions.add(method.getReturnType());
                interactions.addAll(Set.of(method.getParameterTypes()));
              });

      return interactions.stream()
          .filter(ReflectionsConceptFinder::isDDDAnnotated)
          .map(ReflectionsConceptFinder::toCanonicalName)
          .collect(Collectors.toUnmodifiableSet());
    } catch (ClassNotFoundException e) {
      throw new ExtractionException(e.getMessage());
    }
  }

  private static boolean isDDDAnnotated(Class<?> clazz) {
    Set<Annotation> annotations = Set.of(clazz.getAnnotations());
    return annotations.stream()
        .anyMatch(annotation -> DDDKeywords.KEYWORDS.contains(annotation.annotationType()));
  }
}
