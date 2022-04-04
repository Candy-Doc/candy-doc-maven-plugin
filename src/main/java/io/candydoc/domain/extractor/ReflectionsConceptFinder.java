package io.candydoc.domain.extractor;

import org.reflections8.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ReflectionsConceptFinder implements ConceptFinder {

  @Override
  public Set<Class<?>> findConcepts(String packageToScan, Class<? extends Annotation> conceptClass) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(conceptClass);
  }
}
