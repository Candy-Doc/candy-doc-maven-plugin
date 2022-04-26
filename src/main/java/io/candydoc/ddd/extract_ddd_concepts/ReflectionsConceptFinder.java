package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.annotations.DDDKeywords;
import io.candydoc.ddd.model.DDDConcept;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ReflectionsConceptFinder extends DDDConceptFinder {

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
}
