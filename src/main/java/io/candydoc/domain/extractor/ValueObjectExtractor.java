package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.candydoc.domain.repository.ClassesFinder;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import javax.lang.model.element.TypeElement;

@Slf4j
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {

  public List<DomainEvent> extract(ExtractValueObjects command) {
    Set<TypeElement> valueObjectClasses =
        ClassesFinder.getInstance().getClassesAnnotatedBy(io.candydoc.domain.annotations.ValueObject.class);
    log.info("Value objects found in {}: {}", command.getPackageToScan(), valueObjectClasses);
    return valueObjectClasses.stream()
        .filter(valueObject -> !isAnonymous(valueObject))
        .map(
            valueObject ->
                ValueObjectFound.builder()
                    .description(
                        valueObject
                            .getAnnotation(io.candydoc.domain.annotations.ValueObject.class)
                            .description())
                    .name(valueObject.getSimpleName().toString())
                    .className(valueObject.getSimpleName().toString())
                    .packageName(valueObject.getClass().getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isAnonymous(TypeElement valueObject) {
    return valueObject.getClass().isAnonymousClass();
  }
}
