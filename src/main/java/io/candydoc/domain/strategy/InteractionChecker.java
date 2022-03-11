package io.candydoc.domain.strategy;

import io.candydoc.domain.annotations.*;
import io.candydoc.domain.command.CheckConceptInteractions;
import io.candydoc.domain.exceptions.ClassNotAnnotatedByDDDConcept;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InteractionChecker {

    private final Map<Class<?>, InteractionStrategy> interactionStrategies = Map.of(
        CoreConcept.class, new CoreConceptInteractionStrategy(),
        ValueObject.class, new ValueObjectInteractionStrategy(),
        DomainCommand.class, new DomainCommandInteractionStrategy(),
        DomainEvent.class, new DomainEventInteractionStrategy(),
        BoundedContext.class, new BoundedContextInteractionStrategy(),
        Aggregate.class, new AggregatesInteractionStrategy());

    @SneakyThrows
    public List<io.candydoc.domain.events.DomainEvent> check(CheckConceptInteractions command) {
        Class<?> conceptClass = Class.forName(command.getClassName(), true, Thread.currentThread().getContextClassLoader());
        Annotation dddAnnotation = conceptTypeFor(conceptClass);
        return strategyFor(dddAnnotation).checkInteractions(conceptClass);
    }

    private Annotation conceptTypeFor(Class<?> concept) {
        return Arrays.stream(concept.getAnnotations())
            .filter(InteractionChecker::dddAnnotationOnly)
            .findFirst()
            .orElseGet(() -> conceptTypeForSuperClassOf(concept));
    }

    private Annotation conceptTypeForSuperClassOf(Class<?> concept) {
        return Arrays.stream(concept.getSuperclass().getAnnotations())
            .filter(InteractionChecker::dddAnnotationOnly)
            .findFirst()
            .orElseThrow(() -> new ClassNotAnnotatedByDDDConcept(concept));
    }

    private static Boolean dddAnnotationOnly(Annotation annotation) {
        return InteractionStrategy.DDD_ANNOTATION_CLASSES.contains(annotation.annotationType());
    }

    private InteractionStrategy strategyFor(Annotation annotation) {
        return interactionStrategies.get(annotation.annotationType());
    }
}
