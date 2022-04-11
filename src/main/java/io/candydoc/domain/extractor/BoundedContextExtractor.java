package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDDDConcepts;
import io.candydoc.domain.events.BoundedContextFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.model.DDDConcept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BoundedContextExtractor implements Extractor<ExtractDDDConcepts> {
  private final ConceptFinder conceptFinder;

  @Override
  public List<DomainEvent> extract(ExtractDDDConcepts command) {
    return command.getPackagesToScan().stream()
        .map(this::extractBoundedContexts)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<DomainEvent> extractBoundedContexts(String packageToScan) {
    if (packageToScan.isBlank()) {
      throw new DocumentationGenerationFailed(
          "Empty parameters for 'packagesToScan'. Check your pom configuration");
    }
    Set<DDDConcept> boundedContextClasses = conceptFinder.findBoundedContext(packageToScan);
    if (boundedContextClasses.isEmpty()) {
      throw new NoBoundedContextFound(packageToScan);
    }
    log.info("Bounded contexts found in {}: {}", packageToScan, boundedContextClasses);
    return boundedContextClasses.stream()
        .map(
            boundedContext ->
                BoundedContextFound.builder()
                    .name(boundedContext.getName())
                    .packageName(boundedContext.getPackageName())
                    .description(boundedContext.getDescription())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
