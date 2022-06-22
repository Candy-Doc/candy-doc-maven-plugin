package io.candydoc.ddd.domain_event;

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
public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDomainEvents command) {
    String packageToScan = command.getPackageToScan();
    Set<DomainEvent> domainEventClasses =
        DDDConceptFinder.findDomainEvents(PackageName.of(packageToScan));
    log.info("Domain events found in {}: {}", packageToScan, domainEventClasses);
    return domainEventClasses.stream()
        .map(
            domainEvent ->
                DomainEventFound.builder()
                    .description(domainEvent.getDescription().value())
                    .simpleName(domainEvent.getSimpleName().value())
                    .canonicalName(domainEvent.getCanonicalName().value())
                    .packageName(domainEvent.getPackageName().value())
                    .domainContext(packageToScan)
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
