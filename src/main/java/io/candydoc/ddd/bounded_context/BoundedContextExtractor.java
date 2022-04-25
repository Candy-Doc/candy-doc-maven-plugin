package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DocumentationGenerationFailed;
import io.candydoc.ddd.extract_ddd_concepts.ExtractDDDConcepts;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.domain.model.DDDConcept;
import io.candydoc.domain.model.DDDConceptRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BoundedContextExtractor implements Extractor<ExtractDDDConcepts> {
  private final io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDDDConcepts command) {
    return command.getPackagesToScan().stream()
        .map(this::extractBoundedContexts)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<Event> extractBoundedContexts(String packageToScan) {
    if (packageToScan.isBlank()) {
      // Todo: Manque une exception plus précise
      throw new DocumentationGenerationFailed(
          "Empty parameters for 'packagesToScan'. Check your pom configuration");
    }
    Set<DDDConcept> boundedContextClasses = DDDConceptFinder.findBoundedContexts(packageToScan);
    if (boundedContextClasses.isEmpty()) {
      throw new NoBoundedContextFound(packageToScan);
    }
    DDDConceptRepository.getInstance().addDDDConcepts(boundedContextClasses);
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
