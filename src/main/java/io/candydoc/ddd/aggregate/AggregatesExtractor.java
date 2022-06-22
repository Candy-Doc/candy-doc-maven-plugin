package io.candydoc.ddd.aggregate;

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
public class AggregatesExtractor implements Extractor<ExtractAggregates> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractAggregates command) {
    Set<Aggregate> aggregatesClasses =
        DDDConceptFinder.findAggregates(PackageName.of(command.getPackageToScan()));
    log.info("Aggregates found in {}: {}", command.getPackageToScan(), aggregatesClasses);
    return aggregatesClasses.stream()
        .map(aggregate -> toAggregateFound(command, aggregate))
        .collect(Collectors.toUnmodifiableList());
  }

  private AggregateFound toAggregateFound(ExtractAggregates command, Aggregate aggregate) {
    return AggregateFound.builder()
        .canonicalName(aggregate.getCanonicalName().value())
        .simpleName(aggregate.getSimpleName().value())
        .description(aggregate.getDescription().value())
        .packageName(aggregate.getPackageName().value())
        .domainContext(command.getPackageToScan())
        .build();
  }
}
