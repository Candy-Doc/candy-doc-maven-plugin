package io.candydoc.ddd.interaction;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.aggregate.AggregatesInteractionStrategy;
import io.candydoc.ddd.annotations.*;
import io.candydoc.ddd.bounded_context.BoundedContextInteractionStrategy;
import io.candydoc.ddd.core_concept.CoreConceptInteractionStrategy;
import io.candydoc.ddd.domain_command.DomainCommandInteractionStrategy;
import io.candydoc.ddd.domain_event.DomainEventInteractionStrategy;
import io.candydoc.ddd.value_object.ValueObjectInteractionStrategy;
import io.candydoc.domain.model.DDDAnnotation;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDConceptRepository;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InteractionChecker {

  private final Map<Class<? extends Annotation>, InteractionStrategy> interactionStrategies =
      Map.of(
          CoreConcept.class, new CoreConceptInteractionStrategy(),
          ValueObject.class, new ValueObjectInteractionStrategy(),
          DomainCommand.class, new DomainCommandInteractionStrategy(),
          DomainEvent.class, new DomainEventInteractionStrategy(),
          BoundedContext.class, new BoundedContextInteractionStrategy(),
          Aggregate.class, new AggregatesInteractionStrategy());

  @SneakyThrows
  public List<Event> check(CheckConceptInteractions command) {
    DDDConcept concept = DDDConceptRepository.getInstance().findDDDConcept(command.getClassName());
    DDDAnnotation dddAnnotation = conceptTypeFor(concept);
    return strategyFor(dddAnnotation).checkInteractions(concept);
  }

  private DDDAnnotation conceptTypeFor(DDDConcept concept) {
    if (InteractionStrategy.DDD_ANNOTATION_CLASSES.contains(
        concept.getDddAnnotation().getAnnotation())) {
      return concept.getDddAnnotation();
    } else return conceptTypeForSuperClassOf(concept);
  }

  private DDDAnnotation conceptTypeForSuperClassOf(DDDConcept concept) {
    Annotation annotation =
        Arrays.stream(concept.getParent().getAnnotations())
            .filter(InteractionChecker::dddAnnotationOnly)
            .findFirst()
            .orElseThrow(() -> new ConceptNotAnnotatedByDDDConcept(concept));
    return DDDAnnotation.builder().annotation(annotation.annotationType()).build();
  }

  private static Boolean dddAnnotationOnly(Annotation annotation) {
    return InteractionStrategy.DDD_ANNOTATION_CLASSES.contains(annotation.annotationType());
  }

  private InteractionStrategy strategyFor(DDDAnnotation annotation) {
    return interactionStrategies.get(annotation.getAnnotation());
  }
}
