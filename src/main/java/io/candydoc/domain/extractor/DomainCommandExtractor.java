package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.candydoc.domain.repository.ClassesFinder;

import javax.lang.model.element.Element;

public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  @Override
  public List<DomainEvent> extract(ExtractDomainCommands command) {
    Set<Element> domainCommandClasses = ClassesFinder.getInstance().getClassesAnnotatedBy(io.candydoc.domain.annotations.DomainCommand.class);
    return domainCommandClasses.stream()
        .map(
            domainCommand ->
                DomainCommandFound.builder()
                    .description(
                        domainCommand
                            .getAnnotation(io.candydoc.domain.annotations.DomainCommand.class)
                            .description())
                    .name(domainCommand.getSimpleName().toString())
                    .className(domainCommand.getSimpleName().toString())
                    .packageName(domainCommand.getClass().getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
