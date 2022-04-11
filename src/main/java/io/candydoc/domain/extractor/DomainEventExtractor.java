package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainEvents;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import io.candydoc.domain.model.DDDConcept;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  private final DDDConceptFinder DDDConceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractDomainEvents command) {
    Set<DDDConcept> domainEventClasses =
        DDDConceptFinder.findDomainEvents(command.getPackageToScan());
    log.info("Domain events found in {}: {}", command.getPackageToScan(), domainEventClasses);
    return domainEventClasses.stream()
        .map(
            domainEvent ->
                DomainEventFound.builder()
                    .description(domainEvent.getDescription())
                    .name(domainEvent.getName())
                    .className(domainEvent.getCanonicalName())
                    .packageName(domainEvent.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
