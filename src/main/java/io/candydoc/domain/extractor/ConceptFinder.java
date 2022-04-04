package io.candydoc.domain.extractor;

import java.util.Set;

public interface ConceptFinder {
  Set<Class<?>> findConcepts(String packageToScan);

}
