package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {

  public List<DomainEvent> extract(ExtractValueObjects command) {
    Reflections reflections = new Reflections(command.getPackageToScan());
    Set<Class<?>> valueObjectClasses =
        reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.ValueObject.class);
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
