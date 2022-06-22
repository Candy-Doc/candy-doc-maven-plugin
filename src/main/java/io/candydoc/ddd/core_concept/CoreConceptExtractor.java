package io.candydoc.ddd.core_concept;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.ddd.model.PackageName;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CoreConceptExtractor implements Extractor<ExtractCoreConcepts> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractCoreConcepts command) {
    String packageToScan = command.getPackageToScan();
    Set<CoreConcept> coreConcepts =
        DDDConceptFinder.findCoreConcepts(PackageName.of(packageToScan));
    log.info("Core concepts found in {}: {}", packageToScan, coreConcepts);
    List<CoreConceptFound> coreConceptsFound = findCoreConcepts(packageToScan, coreConcepts);
    List<Event> conflicts = checkConflictBetweenCoreConcepts(coreConcepts);
    return Stream.of(coreConceptsFound, conflicts)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  private List<Event> checkConflictBetweenCoreConcepts(Set<CoreConcept> coreConcepts) {
    return coreConcepts.stream()
        .collect(Collectors.groupingBy(CoreConcept::getSimpleName))
        .values()
        .stream()
        .filter(coreConceptFounds -> coreConceptFounds.size() > 1)
        .map(this::toNameConflictBetweenCoreConcepts)
        .collect(Collectors.toUnmodifiableList());
  }

  private NameConflictBetweenCoreConcepts toNameConflictBetweenCoreConcepts(
      List<CoreConcept> duplicateConcepts) {
    return NameConflictBetweenCoreConcepts.builder()
        .coreConcepts(
            duplicateConcepts.stream()
                .map(CoreConcept::getCanonicalName)
                .map(CanonicalName::value)
                .collect(Collectors.toList()))
        .build();
  }

  private List<CoreConceptFound> findCoreConcepts(
      String boundedContextName, Set<CoreConcept> coreConcepts) {
    return coreConcepts.stream()
        .map(coreConcept -> toCoreConceptFound(boundedContextName, coreConcept))
        .collect(Collectors.toUnmodifiableList());
  }

  private CoreConceptFound toCoreConceptFound(String boundedContextName, CoreConcept coreConcept) {
    return CoreConceptFound.builder()
        .simpleName(coreConcept.getSimpleName().value())
        .description(coreConcept.getDescription().value())
        .canonicalName(coreConcept.getCanonicalName().value())
        .packageName(coreConcept.getPackageName().value())
        .domainContext(boundedContextName)
        .build();
  }
}
