package io.candydoc.domain.strategy;

import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.InteractionBetweenConceptFound;
import io.candydoc.domain.repository.ProcessorUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ValueObjectInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Element concept) {
    if (!extractDDDInteractions(concept).isEmpty()) {
      return List.of(
          ConceptRuleViolated.builder()
              .className(concept.getSimpleName().toString())
              .reason("Value Object should only contain primitive types")
              .build());
    }
    return List.of();
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
                  Set<Element> parameterClasses = ((ExecutableElement) method).getTypeParameters().stream().collect(Collectors.toSet());
                  Element returnType = ProcessorUtils.getInstance().getTypesUtils().asElement(
                      ((ExecutableElement) method).getReturnType()
                  );
                  if(returnType != null) parameterClasses.add(returnType);
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
                    .anyMatch(annotationType -> classInCurrentConcept.getAnnotation(annotationType) != null)
        )
        .map(
            interactingConcept ->
                InteractionBetweenConceptFound.builder()
                    .from(currentConcept.getSimpleName().toString())
                    .with(interactingConcept.getSimpleName().toString())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }
}
