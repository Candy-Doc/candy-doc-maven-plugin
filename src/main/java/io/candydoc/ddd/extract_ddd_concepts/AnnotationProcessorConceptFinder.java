package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.repository.ClassesFinder;
import io.candydoc.ddd.repository.ProcessorUtils;
import io.candydoc.ddd.value_object.ValueObject;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class AnnotationProcessorConceptFinder implements DDDConceptFinder {

  public static final Map<Class<? extends Annotation>, Function<Element, DDDConcept>>
      ANNOTATION_PROCESSORS =
          Map.of(
              io.candydoc.ddd.annotations.BoundedContext.class,
              AnnotationProcessorConceptFinder::toBoundedContext,
              io.candydoc.ddd.annotations.CoreConcept.class,
              AnnotationProcessorConceptFinder::toCoreConcept,
              io.candydoc.ddd.annotations.ValueObject.class,
              AnnotationProcessorConceptFinder::toValueObject,
              io.candydoc.ddd.annotations.DomainEvent.class,
              AnnotationProcessorConceptFinder::toDomainEvent,
              io.candydoc.ddd.annotations.DomainCommand.class,
              AnnotationProcessorConceptFinder::toDomainCommand,
              io.candydoc.ddd.annotations.Aggregate.class,
              AnnotationProcessorConceptFinder::toAggregate);

  private static Set<DDDConcept> foundConcepts;

  @Override
  public Set<DDDConcept> findDDDConcepts() {
    if (foundConcepts == null) {
      foundConcepts =
          DDDKeywords.KEYWORDS.stream()
              .flatMap(
                  annotation -> {
                    Function<Element, DDDConcept> processor = ANNOTATION_PROCESSORS.get(annotation);

                    return ClassesFinder.getInstance().getElementsAnnotatedBy(annotation).stream()
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

  private static Aggregate toAggregate(Element element) {
    String simpleName = element.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).name();
    if (simpleName.isBlank()) {
      simpleName = element.getSimpleName().toString();
    }

    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.Aggregate.class).description();

    return Aggregate.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(simpleName))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static BoundedContext toBoundedContext(Element element) {
    String simpleName =
        element.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).name();
    if (simpleName.isBlank()) {
      simpleName = element.getSimpleName().toString();
    }

    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.BoundedContext.class).description();

    return BoundedContext.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(simpleName))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static CoreConcept toCoreConcept(Element element) {
    String simpleName = element.getAnnotation(io.candydoc.ddd.annotations.CoreConcept.class).name();
    if (simpleName.isBlank()) {
      simpleName = element.getSimpleName().toString();
    }

    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.CoreConcept.class).description();

    return CoreConcept.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(simpleName))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static DomainCommand toDomainCommand(Element element) {
    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.DomainCommand.class).description();

    return DomainCommand.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(element))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static DomainEvent toDomainEvent(Element element) {
    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.DomainEvent.class).description();

    return DomainEvent.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(element))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static ValueObject toValueObject(Element element) {
    String description =
        element.getAnnotation(io.candydoc.ddd.annotations.ValueObject.class).description();

    return ValueObject.builder()
        .canonicalName(toCanonicalName(element))
        .simpleName(toSimpleName(element))
        .packageName(toPackageName(element))
        .description(Description.of(description))
        .build();
  }

  private static CanonicalName toCanonicalName(Element element) {
    return CanonicalName.of(element.asType().toString());
  }

  private static SimpleName toSimpleName(Element element) {
    return SimpleName.of(element.getSimpleName().toString());
  }

  private static SimpleName toSimpleName(String simpleName) {
    return SimpleName.of(simpleName);
  }

  private static PackageName toPackageName(Element element) {
    return PackageName.of(
        ProcessorUtils.getInstance()
            .getElementUtils()
            .getPackageOf(element)
            .getSimpleName()
            .toString());
  }

  private static Set<CanonicalName> conceptsInteractingWith(CanonicalName canonicalName) {
    Element element = ClassesFinder.getInstance().forName(canonicalName);
    Set<Element> interactions = new HashSet<>();

    element.getEnclosedElements().stream()
        .filter(enclosedElement -> enclosedElement instanceof VariableElement)
        .forEach(interactions::add);

    element.getEnclosedElements().stream()
        .filter(enclosedElement -> enclosedElement instanceof ExecutableElement)
        .forEach(
            method -> {
              interactions.add(
                  ProcessorUtils.getInstance()
                      .getTypesUtils()
                      .asElement(((ExecutableElement) method).getReturnType()));
              interactions.addAll(
                  ((ExecutableElement) method)
                      .getTypeParameters().stream().collect(Collectors.toSet()));
            });

    return interactions.stream()
        .filter(AnnotationProcessorConceptFinder::isDDDAnnotated)
        .map(AnnotationProcessorConceptFinder::toCanonicalName)
        .collect(Collectors.toUnmodifiableSet());
  }

  private static boolean isDDDAnnotated(Element element) {
    return element.getAnnotationMirrors().stream()
        .anyMatch(
            annotation -> DDDKeywords.KEYWORDS.contains(annotation.getAnnotationType().toString()));
  }
}
