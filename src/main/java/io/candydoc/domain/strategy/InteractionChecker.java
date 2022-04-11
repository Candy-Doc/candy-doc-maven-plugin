package io.candydoc.domain.strategy;

import io.candydoc.domain.annotations.*;
import io.candydoc.domain.command.CheckConceptInteractions;
import io.candydoc.domain.exceptions.ElementNotAnnotatedByDDDConcept;
import io.candydoc.domain.repository.ProcessorUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import lombok.SneakyThrows;

public class InteractionChecker {

  private final Map<Class<?>, InteractionStrategy> interactionStrategies =
      Map.of(
          CoreConcept.class, new CoreConceptInteractionStrategy(),
          ValueObject.class, new ValueObjectInteractionStrategy(),
          DomainCommand.class, new DomainCommandInteractionStrategy(),
          DomainEvent.class, new DomainEventInteractionStrategy(),
          BoundedContext.class, new BoundedContextInteractionStrategy(),
          Aggregate.class, new AggregatesInteractionStrategy());

  @SneakyThrows
  public List<io.candydoc.domain.events.DomainEvent> check(CheckConceptInteractions command) {
    TypeElement conceptClass =
        ProcessorUtils.getInstance().getElementUtils().getTypeElement(command.getClassName());
    ProcessorUtils.getInstance()
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "Checking concept " + conceptClass);
    AnnotationMirror dddAnnotation = conceptTypeFor(conceptClass);
    ProcessorUtils.getInstance()
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "Found DDD annotation in it: " + dddAnnotation);
    InteractionStrategy strategy = strategyFor(dddAnnotation);
    ProcessorUtils.getInstance()
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "Strategy: " + strategy);
    return strategy.checkInteractions(conceptClass);
  }

  private AnnotationMirror conceptTypeFor(TypeElement concept) {
    List<AnnotationMirror> annotationMirrors =
        concept.getAnnotationMirrors().stream()
            .filter(InteractionChecker::dddAnnotationOnly)
            .collect(Collectors.toList());
    if (annotationMirrors.isEmpty()) return conceptTypeForSuperClassOf(concept);
    else return annotationMirrors.get(0);
  }

  private AnnotationMirror conceptTypeForSuperClassOf(TypeElement concept) {
    return concept.getSuperclass().getAnnotationMirrors().stream()
        .filter(InteractionChecker::dddAnnotationOnly)
        .findFirst()
        .orElseThrow(() -> new ElementNotAnnotatedByDDDConcept(concept));
  }

  private static Boolean dddAnnotationOnly(AnnotationMirror annotation) {
    return InteractionStrategy.DDD_ANNOTATION_CLASSES.stream()
        .anyMatch(
            annotationType ->
                annotationType
                    .getCanonicalName()
                    .equals(annotation.getAnnotationType().toString()));
  }

  private InteractionStrategy strategyFor(AnnotationMirror annotation) {
    return interactionStrategies.entrySet().stream()
        .filter(
            entry ->
                entry.getKey().getCanonicalName().equals(annotation.getAnnotationType().toString()))
        .findFirst()
        .orElseThrow(
            () -> new ElementNotAnnotatedByDDDConcept(annotation.getAnnotationType().asElement()))
        .getValue();
  }
}
