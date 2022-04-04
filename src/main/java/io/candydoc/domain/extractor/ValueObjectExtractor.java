package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {
  private final ConceptFinder conceptFinder;

  public List<DomainEvent> extract(ExtractValueObjects command) {
    Set<Class<?>> valueObjectClasses = conceptFinder.findConcepts(command.getPackageToScan(), io.candydoc.domain.annotations.ValueObject.class);
    log.info("Value objects found in {}: {}", command.getPackageToScan(), valueObjectClasses);
    return valueObjectClasses.stream()
        .filter(valueObject -> !isAnonymous(valueObject))
        .map(
            valueObject ->
                ValueObjectFound.builder()
                    .description(getDescription(valueObject))
                    .name(getSimpleName(valueObject))
                    .className(valueObject.getName())
                    .packageName(valueObject.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isAnonymous(Class<?> valueObject) {
    return valueObject.isAnonymousClass();
  }

  private String getSimpleName(Class<?> valueObject) {
    return valueObject.getSimpleName();
  }

  private String getDescription(Class<?> valueObject) {
    io.candydoc.domain.annotations.ValueObject annotation =
        valueObject.getAnnotation(io.candydoc.domain.annotations.ValueObject.class);
    return annotation.description();
  }
}
