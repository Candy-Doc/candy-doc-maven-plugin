package io.candydoc.domain.extractor;

import io.candydoc.domain.command.ExtractDDDConcepts;
import io.candydoc.domain.events.BoundedContextFound;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import org.reflections8.Reflections;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            throw new DocumentationGenerationFailed("Empty parameters for 'packagesToScan'. Check your pom configuration");
        }
        Reflections reflections = new Reflections(packageToScan);
        Set<Class<?>> rawBoundedContexts = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);
        if (rawBoundedContexts.isEmpty()) {
            throw new NoBoundedContextFound(packageToScan);
        }
        return rawBoundedContexts.stream()
            .map(boundedContext -> BoundedContextFound.builder()
                .name(boundedContext.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).name())
                .packageName(boundedContext.getPackageName())
                .description(boundedContext.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).description())
                .build())
            .collect(Collectors.toUnmodifiableList());
    }
}
