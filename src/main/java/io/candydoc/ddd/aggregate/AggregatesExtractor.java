package io.candydoc.ddd.aggregate;

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
public class AggregatesExtractor implements Extractor<ExtractAggregates> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractAggregates command) {
    Set<DDDConcept> aggregatesClasses = DDDConceptFinder.findAggregates(command.getPackageToScan());
    log.info("Aggregates found in {}: {}", command.getPackageToScan(), aggregatesClasses);
    DDDConceptRepository.getInstance().addDDDConcepts(aggregatesClasses);
    return aggregatesClasses.stream()
        .map(
            aggregate ->
                AggregateFound.builder()
                    .name(getSimpleName(aggregate))
                    .description(aggregate.getDescription())
                    .className(aggregate.getCanonicalName())
                    .packageName(aggregate.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private String getSimpleName(DDDConcept aggregate) {
    String annotatedName = aggregate.getName();
    return annotatedName.isBlank() ? aggregate.getCanonicalName() : annotatedName;
  }
}
