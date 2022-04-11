package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcepts;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.NameConflictBetweenCoreConcepts;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.candydoc.domain.model.DDDConcept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CoreConceptExtractor implements Extractor<ExtractCoreConcepts> {

  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractCoreConcepts command) {
    Set<DDDConcept> coreConceptClasses = conceptFinder.findCoreConcept(command.getPackageToScan());
    log.info("Core concepts found in {}: {}", command.getPackageToScan(), coreConceptClasses);
    List<CoreConceptFound> coreConcepts = findCoreConcepts(command, coreConceptClasses);
    List<DomainEvent> conflicts = checkConflictBetweenCoreConcepts(coreConcepts);
    return Stream.of(coreConcepts, conflicts)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  private List<DomainEvent> checkConflictBetweenCoreConcepts(
      List<CoreConceptFound> foundCoreConcepts) {
    return foundCoreConcepts.stream()
        .collect(Collectors.groupingBy(CoreConceptFound::getName))
        .values()
        .stream()
        .filter(coreConceptFounds -> coreConceptFounds.size() > 1)
        .map(
            duplicateConcepts ->
                NameConflictBetweenCoreConcepts.builder()
                    .coreConceptClassNames(
                        duplicateConcepts.stream()
                            .map(CoreConceptFound::getClassName)
                            .collect(Collectors.toList()))
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private List<CoreConceptFound> findCoreConcepts(
      ExtractCoreConcepts command, Set<DDDConcept> coreConceptClasses) {
    return coreConceptClasses.stream()
        .map(
            coreConcept ->
                CoreConceptFound.builder()
                    .name(getSimpleName(coreConcept))
                    .description(getDescription(coreConcept))
                    .className(coreConcept.getCanonicalName())
                    .packageName(coreConcept.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private String getSimpleName(DDDConcept aggregate) {
    String annotatedName = aggregate.getName();
    return annotatedName.isBlank() ? aggregate.getCanonicalName() : annotatedName;
  }

  private String getDescription(DDDConcept coreConcept) {
    return coreConcept.getDescription();
  }
}
