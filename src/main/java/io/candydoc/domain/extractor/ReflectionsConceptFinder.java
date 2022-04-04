package io.candydoc.domain.extractor;

import org.reflections8.Reflections;

import java.util.Set;

public class ReflectionsConceptFinder implements ConceptFinder {

  @Override
  public Set<Class<?>> findConcepts(String packageToScan) {
    Reflections reflections = new Reflections(packageToScan);
    return reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);
  }
}
