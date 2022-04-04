package io.candydoc.domain.extractor;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ConceptFinder {
  Set<Class<?>> findConcepts(String packageToScan, Class<? extends Annotation> conceptClass);
}
