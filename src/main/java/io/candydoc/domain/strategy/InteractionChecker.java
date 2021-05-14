package io.candydoc.domain.strategy;

import io.candydoc.domain.annotations.*;
import io.candydoc.domain.command.CheckConceptInteraction;
import io.candydoc.domain.exceptions.ClassNotAnnotatedByDDDConcept;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.util.*;

public class InteractionChecker {

    private final Map<Class<?>, InteractionStrategy> interactionStrategies = Map.of(
            CoreConcept.class, new CoreConceptInteractionStrategy(),
            ValueObject.class, new ValueObjectInteractionStrategy(),
            DomainCommand.class, new DomainCommandInteractionStrategy(),
            DomainEvent.class, new DomainCommandInteractionStrategy(),
            BoundedContext.class, new BoundedContextInteractionStrategy());

    @SneakyThrows
    public List<io.candydoc.domain.events.DomainEvent> check(CheckConceptInteraction command) {
        Class<?> conceptClass = Class.forName(command.getClassName(), true, Thread.currentThread().getContextClassLoader());
        Annotation dddAnnotation = conceptTypeFor(conceptClass);
        return strategyFor(dddAnnotation).checkInteractions(conceptClass);
    }

    private Annotation conceptTypeFor(Class<?> concept) {
        return Arrays.stream(concept.getAnnotations()).filter(InteractionChecker::dddAnnotationOnly)
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
