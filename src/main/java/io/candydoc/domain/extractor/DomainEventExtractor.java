package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainEvents;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  @Override
  public List<DomainEvent> extract(ExtractDomainEvents command) {
    Reflections reflections = new Reflections(command.getPackageToScan());
    Set<Class<?>> domainEventClasses =
        reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.DomainEvent.class);
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
