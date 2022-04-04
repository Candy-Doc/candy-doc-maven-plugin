package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainEvents;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractDomainEvents command) {
    Set<Class<?>> domainEventClasses = conceptFinder.findConcepts(command.getPackageToScan(), io.candydoc.domain.annotations.DomainEvent.class);
    log.info("Domain events found in {}: {}", command.getPackageToScan(), domainEventClasses);
    return domainEventClasses.stream()
        .map(
            domainEvent ->
                DomainEventFound.builder()
                    .description(
                        domainEvent
                            .getAnnotation(io.candydoc.domain.annotations.DomainEvent.class)
                            .description())
                    .name(domainEvent.getSimpleName())
                    .className(domainEvent.getName())
                    .packageName(domainEvent.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
