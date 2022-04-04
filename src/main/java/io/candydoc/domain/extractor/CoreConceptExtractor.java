package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcepts;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.NameConflictBetweenCoreConcepts;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
@RequiredArgsConstructor
public class CoreConceptExtractor implements Extractor<ExtractCoreConcepts> {

  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractCoreConcepts command) {
    Set<Class<?>> coreConceptClasses = conceptFinder.findConcepts(command.getPackageToScan(), io.candydoc.domain.annotations.CoreConcept.class);
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
      ExtractCoreConcepts command, Set<Class<?>> coreConceptClasses) {
    return coreConceptClasses.stream()
        .filter(coreConcept -> !isAnonymous(coreConcept))
        .map(
            coreConcept ->
                CoreConceptFound.builder()
                    .name(getSimpleName(coreConcept))
                    .description(getDescription(coreConcept))
                    .className(coreConcept.getName())
                    .packageName(coreConcept.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isAnonymous(Class<?> coreConcept) {
    return coreConcept.isAnonymousClass();
  }

  private String getSimpleName(Class<?> aggregate) {
    io.candydoc.domain.annotations.CoreConcept annotation =
        aggregate.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class);
    String annotatedName = annotation.name();
    return annotatedName.isBlank() ? aggregate.getSimpleName() : annotatedName;
  }

  private String getDescription(Class<?> coreConcept) {
    io.candydoc.domain.annotations.CoreConcept annotation =
        coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class);
    return annotation.description();
  }
}
