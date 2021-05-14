package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractCoreConcept;
import io.candydoc.domain.command.ExtractDomainEvent;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import org.reflections8.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class DomainEventExtractor implements Extractor<ExtractDomainEvent> {

    @Override
    public List<DomainEvent> extract(ExtractDomainEvent command) {
        Reflections reflections = new Reflections(command.getPackageToScan());
        Set<Class<?>> domainEventClasses = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.DomainEvent.class);
        return domainEventClasses.stream()
                .map(domainEvent -> {List<DomainEvent> eventList = new LinkedList<>(Collections.singleton(DomainEventFound.builder()
                        .description(domainEvent.getAnnotation(io.candydoc.domain.annotations.DomainEvent.class).description())
                        .className(domainEvent.getName())
                        .boundedContext(command.getPackageToScan())
                        .build()));
                    return eventList;})
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }
}
