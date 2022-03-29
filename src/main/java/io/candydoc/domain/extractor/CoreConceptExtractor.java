package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcepts;
import io.candydoc.domain.events.CoreConceptFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.NameConflictBetweenCoreConcepts;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.candydoc.domain.repository.ClassesFinder;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.TypeElement;

@Slf4j
public class CoreConceptExtractor implements Extractor<ExtractCoreConcepts> {

  @Override
  public List<DomainEvent> extract(ExtractCoreConcepts command) {
    Set<TypeElement> coreConceptClasses = ClassesFinder.getInstance().getClassesAnnotatedBy(io.candydoc.domain.annotations.CoreConcept.class);
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
      ExtractCoreConcepts command, Set<TypeElement> coreConceptClasses) {
    return coreConceptClasses.stream()
        .filter(coreConcept -> !isAnonymous(coreConcept))
        .map(
            coreConcept ->
                CoreConceptFound.builder()
                    .name(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).name())
                    .description(coreConcept.getAnnotation(io.candydoc.domain.annotations.CoreConcept.class).description())
                    .className(coreConcept.getSimpleName().toString())
                    .packageName(coreConcept.getClass().getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isAnonymous(TypeElement coreConcept) {
    return coreConcept.getClass().isAnonymousClass();
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
