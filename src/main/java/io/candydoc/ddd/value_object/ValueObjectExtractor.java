package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.ddd.model.PackageName;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ValueObjectExtractor implements Extractor<ExtractValueObjects> {
  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  public List<Event> extract(ExtractValueObjects command) {
    Set<ValueObject> valueObjectClasses =
        DDDConceptFinder.findValueObjects(PackageName.of(command.getPackageToScan()));
    log.info("Value objects found in {}: {}", command.getPackageToScan(), valueObjectClasses);
    return valueObjectClasses.stream()
        .map(
            valueObject ->
                ValueObjectFound.builder()
                    .description(valueObject.getDescription().value())
                    .simpleName(valueObject.getSimpleName().value())
                    .canonicalName(valueObject.getCanonicalName().value())
                    .packageName(valueObject.getPackageName().value())
                    .domainContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
