package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.aggregate.Aggregate;
import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.bounded_context.BoundedContext;
import io.candydoc.ddd.core_concept.CoreConcept;
import io.candydoc.ddd.domain_command.DomainCommand;
import io.candydoc.ddd.domain_event.DomainEvent;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.value_object.ValueObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ReflectionsConceptFinder extends DDDConceptFinder {

  private static Set<DDDConcept> foundConcepts;

  @Override
  public Set<DDDConcept> findDDDConcepts() {
    if (foundConcepts == null) {
        Reflections reflections = new Reflections();

        foundConcepts = DDDKeywords.KEYWORDS.stream()
            .flatMap(annotation -> {
                Function<Class<?>, DDDConcept> processor = ANNOTATION_PROCESSORS.get(annotation);

                return reflections.getTypesAnnotatedWith(annotation).stream()
                    .filter(clazz -> !clazz.isAnonymousClass())
                    .map(processor);
            })
            .collect(Collectors.toUnmodifiableSet());
    }
    return foundConcepts;
  }
}
