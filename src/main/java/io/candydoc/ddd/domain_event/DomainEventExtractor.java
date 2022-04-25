package io.candydoc.ddd.domain_event;

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
public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDomainEvents command) {
    Set<DDDConcept> domainEventClasses =
        DDDConceptFinder.findDomainEvents(command.getPackageToScan());
    DDDConceptRepository.getInstance().addDDDConcepts(domainEventClasses);
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
