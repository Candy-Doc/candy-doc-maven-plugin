package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDDDConcepts;
import io.candydoc.domain.events.BoundedContextFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

@Slf4j
public class BoundedContextExtractor implements Extractor<ExtractDDDConcepts> {

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
    Reflections reflections = new Reflections(packageToScan);
    Set<Class<?>> boundedContextClasses =
        reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);
    if (boundedContextClasses.isEmpty()) {
      throw new NoBoundedContextFound(packageToScan);
    }
    log.info("Bounded contexts found in {}: {}", packageToScan, boundedContextClasses);
    return boundedContextClasses.stream()
        .map(
            boundedContext ->
                BoundedContextFound.builder()
                    .name(
                        boundedContext
                            .getAnnotation(io.candydoc.domain.annotations.BoundedContext.class)
                            .name())
                    .packageName(boundedContext.getPackageName())
                    .description(
                        boundedContext
                            .getAnnotation(io.candydoc.domain.annotations.BoundedContext.class)
                            .description())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }
}
