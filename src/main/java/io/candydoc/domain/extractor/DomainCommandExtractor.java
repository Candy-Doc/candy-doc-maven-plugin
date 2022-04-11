package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDomainCommands;
import io.candydoc.domain.events.DomainCommandFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.model.DDDConcept;
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
    Set<DDDConcept> domainCommandClasses = conceptFinder.findDomainCommand(command.getPackageToScan());
    log.info("Domain commands found in {}: {}", command.getPackageToScan(), domainCommandClasses);
    return domainCommandClasses.stream()
        .map(
            domainCommand ->
                DomainCommandFound.builder()
                    .description(domainCommand.getDescription())
                    .name(domainCommand.getName())
                    .className(domainCommand.getCanonicalName())
                    .packageName(domainCommand.getPackageName())
                    .boundedContext(command.getPackageToScan())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
