package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainEvents;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.events.DomainEventFound;
import io.candydoc.domain.repository.ClassesFinder;
import io.candydoc.domain.repository.ProcessorUtils;
import java.util.*;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class DomainEventExtractor implements Extractor<ExtractDomainEvents> {

  @Override
  public List<DomainEvent> extract(ExtractDomainEvents command) {
    Set<Element> domainEventClasses =
        ClassesFinder.getInstance()
            .getClassesAnnotatedBy(io.candydoc.domain.annotations.DomainEvent.class);
    return domainEventClasses.stream()
        .map(
            domainEvent ->
                DomainEventFound.builder()
                    .description(
                        domainEvent
                            .getAnnotation(io.candydoc.domain.annotations.DomainEvent.class)
                            .description())
                    .name(domainEvent.getSimpleName().toString())
                    .className(domainEvent.asType().toString())
                    .packageName(
                        ProcessorUtils.getInstance()
                            .getElementUtils()
                            .getPackageOf(domainEvent)
                            .getSimpleName()
                            .toString())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
