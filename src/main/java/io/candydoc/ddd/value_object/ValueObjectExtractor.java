package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDConceptRepository;
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
    Set<DDDConcept> valueObjectClasses =
        DDDConceptFinder.findValueObjects(command.getPackageToScan());
    DDDConceptRepository.getInstance().addDDDConcepts(valueObjectClasses);
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
