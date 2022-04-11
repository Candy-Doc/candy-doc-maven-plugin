package io.candydoc.domain.strategy;

import io.candydoc.domain.annotations.Aggregate;
import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import io.candydoc.domain.repository.ProcessorUtils;
import java.util.*;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class CoreConceptInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Element concept) {
    List<DomainEvent> domainEvents = new LinkedList<>();
    ProcessorUtils.getInstance()
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "Checking core concept interactions");
    domainEvents.addAll(extractWrongInteractions(concept));
    domainEvents.addAll(extractDDDInteractions(concept));
    ProcessorUtils.getInstance()
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "Core concept interactions checked");
    return domainEvents;
  }

  private List<DomainEvent> extractWrongInteractions(Element currentConcept) {
    Set<Element> classesInCurrentConcept =
        currentConcept.getEnclosedElements().stream()
            .filter(element -> element instanceof VariableElement)
            .collect(Collectors.toSet());
    return classesInCurrentConcept.stream()
        .filter(
            classInCurrentConcept -> classInCurrentConcept.getAnnotation(Aggregate.class) != null)
        .map(
            wrongClass ->
                ConceptRuleViolated.builder()
                    .className(currentConcept.getSimpleName().toString())
                    .reason(
                        "CoreConcept interact with Aggregates "
                            + wrongClass.getSimpleName().toString()
                            + ".")
                    .build())
        .collect(Collectors.toList());
  }

  private Set<Element> extractInteractingClasses(Element currentConcept) {
    Set<Element> classesInCurrentConcept =
        currentConcept.getEnclosedElements().stream()
            .filter(element -> element instanceof VariableElement)
            .collect(Collectors.toSet());
    classesInCurrentConcept.addAll(
        currentConcept.getEnclosedElements().stream()
            .filter(element -> element instanceof ExecutableElement)
            .map(
                method -> {
                  Set<Element> parameterClasses =
                      ((ExecutableElement) method)
                          .getTypeParameters().stream().collect(Collectors.toSet());
                  Element returnType =
                      ProcessorUtils.getInstance()
                          .getTypesUtils()
                          .asElement(((ExecutableElement) method).getReturnType());
                  if (returnType != null) parameterClasses.add(returnType);
                  return parameterClasses;
                })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet()));
    return classesInCurrentConcept;
  }

  public Set<InteractionBetweenConceptFound> extractDDDInteractions(Element currentConcept) {
    Set<Element> classesInCurrentConcept = extractInteractingClasses(currentConcept);
    return classesInCurrentConcept.stream()
        .filter(
            classInCurrentConcept ->
                DDD_ANNOTATION_CLASSES.stream()
                    .anyMatch(
                        annotationType ->
                            classInCurrentConcept.getAnnotation(annotationType) != null))
        .map(
            interactingConcept ->
                InteractionBetweenConceptFound.builder()
                    .from(currentConcept.getSimpleName().toString())
                    .with(interactingConcept.getSimpleName().toString())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }
}
