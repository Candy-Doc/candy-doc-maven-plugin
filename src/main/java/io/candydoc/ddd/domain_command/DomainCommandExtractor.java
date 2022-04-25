package io.candydoc.ddd.domain_command;

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
public class DomainCommandExtractor implements Extractor<ExtractDomainCommands> {

  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDomainCommands command) {
    Set<DDDConcept> domainCommandClasses =
        DDDConceptFinder.findDomainCommands(command.getPackageToScan());
    DDDConceptRepository.getInstance().addDDDConcepts(domainCommandClasses);
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
