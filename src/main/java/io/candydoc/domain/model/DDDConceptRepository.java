package io.candydoc.domain.model;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDDConceptRepository {
  private static final DDDConceptRepository INSTANCE = new DDDConceptRepository();
  private Set<DDDConcept> DDDConcepts = new HashSet<>();

  public DDDConceptRepository() {}

  public boolean addDDDConcepts(Set<DDDConcept> DDDConcepts) {
    return this.DDDConcepts.addAll(DDDConcepts);
  }

  public DDDConcept findDDDConcept(String className) {
    for (DDDConcept concept : DDDConcepts) {
      if (concept.getCanonicalName() == className) return concept;
    }
    return null;
  }

  public static DDDConceptRepository getInstance() {
    return INSTANCE;
  }
}
