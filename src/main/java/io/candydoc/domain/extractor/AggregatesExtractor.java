package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractAggregates;
import io.candydoc.domain.events.AggregateFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.model.DDDConcept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AggregatesExtractor implements Extractor<ExtractAggregates> {

  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractAggregates command) {
    Set<DDDConcept> aggregatesClasses = conceptFinder.findAggregate(command.getPackageToScan());
    log.info("Aggregates found in {}: {}", command.getPackageToScan(), aggregatesClasses);
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
