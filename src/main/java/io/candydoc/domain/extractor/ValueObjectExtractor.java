package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractValueObjects;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.ValueObjectFound;
import io.candydoc.domain.model.DDDConcept;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {
  private final DDDConceptFinder DDDConceptFinder;

  public List<DomainEvent> extract(ExtractValueObjects command) {
    Set<DDDConcept> valueObjectClasses =
        DDDConceptFinder.findValueObjects(command.getPackageToScan());
    log.info("Value objects found in {}: {}", command.getPackageToScan(), valueObjectClasses);
    return valueObjectClasses.stream()
        .map(
            valueObject ->
                ValueObjectFound.builder()
                    .description(getDescription(valueObject))
                    .name(getSimpleName(valueObject))
                    .className(valueObject.getCanonicalName())
                    .packageName(valueObject.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private String getSimpleName(DDDConcept valueObject) {
    return valueObject.getName();
  }

  private String getDescription(DDDConcept valueObject) {
    return valueObject.getDescription();
  }
}