package io.candydoc.ddd.domain_command;

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
public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDomainCommands command) {
    String packageToScan = command.getPackageToScan();
    Set<DomainCommand> domainCommands =
        DDDConceptFinder.findDomainCommands(PackageName.of(packageToScan));
    log.info("Domain commands found in {}: {}", packageToScan, domainCommands);
    return domainCommands.stream()
        .map(domainCommand -> toDomainCommandFound(packageToScan, domainCommand))
        .collect(Collectors.toUnmodifiableList());
  }

  private DomainCommandFound toDomainCommandFound(
      String boundedContextName, DomainCommand domainCommand) {
    return DomainCommandFound.builder()
        .description(domainCommand.getDescription().value())
        .simpleName(domainCommand.getSimpleName().value())
        .canonicalName(domainCommand.getCanonicalName().value())
        .packageName(domainCommand.getPackageName().value())
        .boundedContext(boundedContextName)
        .build();
  }
}
