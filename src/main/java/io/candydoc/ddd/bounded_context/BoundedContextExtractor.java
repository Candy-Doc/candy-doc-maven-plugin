package io.candydoc.ddd.bounded_context;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.ExtractDDDConcepts;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.ddd.model.PackageName;
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

  public List<Event> extract(ExtractBoundedContexts command) {
    return extractBoundedContexts(command.getPackageToScan()).stream()
        .collect(Collectors.toUnmodifiableList());
  }

  public List<Event> extractBoundedContexts(String packageToScan) {
    Set<BoundedContext> boundedContextClasses =
        DDDConceptFinder.findBoundedContexts(PackageName.of(packageToScan));
    log.info("Bounded contexts found in {}: {}", packageToScan, boundedContextClasses);
    return boundedContextClasses.stream()
        .map(this::toBoundedContextFound)
        .collect(Collectors.toUnmodifiableList());
  }

  private BoundedContextFound toBoundedContextFound(BoundedContext boundedContext) {
    return BoundedContextFound.builder()
        .simpleName(boundedContext.getSimpleName().value())
        .canonicalName(boundedContext.getCanonicalName().value())
        .packageName(boundedContext.getPackageName().value())
        .description(boundedContext.getDescription().value())
        .subdomainType(boundedContext.getSubdomainType().name())
        .build();
  }
}
