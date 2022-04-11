package io.candydoc.domain.strategy;

import io.candydoc.domain.events.ConceptRuleViolated;
import io.candydoc.domain.events.DomainEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class DomainEventInteractionStrategy implements InteractionStrategy {

  public List<DomainEvent> checkInteractions(Element concept) {
    Set<Element> classesInCurrentConcept =
        concept.getEnclosedElements().stream()
            .filter(element -> element instanceof VariableElement)
            .collect(Collectors.toSet());
    return classesInCurrentConcept.stream()
        .filter(
            classInCurrentConcept ->
                DDD_ANNOTATION_CLASSES.stream()
                    .anyMatch(
                        annotationClass ->
                            classInCurrentConcept.getAnnotation(annotationClass) != null))
        .map(
            match ->
                ConceptRuleViolated.builder()
                    .className(concept.getSimpleName().toString())
                    .reason(
                        "Wrong interaction with concept " + match.getSimpleName().toString() + ".")
                    .build())
        .collect(Collectors.toList());
  }
}
