package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractDomainCommands command) {
    Set<Class<?>> domainCommandClasses = conceptFinder.findConcepts(command.getPackageToScan(), io.candydoc.domain.annotations.DomainCommand.class);
    log.info("Domain commands found in {}: {}", command.getPackageToScan(), domainCommandClasses);
    return domainCommandClasses.stream()
        .map(
            domainCommand ->
                DomainCommandFound.builder()
                    .description(
                        domainCommand
                            .getAnnotation(io.candydoc.domain.annotations.DomainCommand.class)
                            .description())
                    .name(domainCommand.getSimpleName())
                    .className(domainCommand.getName())
                    .packageName(domainCommand.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
