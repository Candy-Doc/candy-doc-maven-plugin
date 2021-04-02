package io.candydoc.domain;

import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.domain.exceptions.DomainException;
import io.candydoc.domain.exceptions.NoBoundedContextFound;
import io.candydoc.domain.exceptions.WrongUsageOfBoundedContext;
import io.candydoc.domain.model.BoundedContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Value
@RequiredArgsConstructor
@Slf4j
public class Domain {

    SaveDocumentationPort saveDocumentationPort;
    Logger logger = Logger.getLogger("logger");

    public void generateDocumentation(GenerateDocumentation command) throws IOException, DomainException {
        checkParameters(command);
        List<BoundedContext> boundedContexts = extractBoundedContexts(command.getPackagesToScan());
        saveDocumentationPort.save(boundedContexts);
        logger.info("Documentation generation has succeeded.");
    }


    public List<BoundedContext> extractBoundedContexts(List<String> packagesToScan) throws DomainException {
        logger.info("Bounded contexts extraction has started...");
        return packagesToScan.stream()
                .map(this::extractBoundedContexts)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<BoundedContext> extractBoundedContexts(String packageToScan) throws DomainException {
        if (packageToScan.isBlank()) {
            throw new DocumentationGenerationFailed("empty parameters for 'packagesToScan'. Check your pom configuation");
        }
        Reflections reflections = new Reflections(packageToScan);
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(io.candydoc.domain.annotations.BoundedContext.class);

        if (set.isEmpty()) {
            throw new NoBoundedContextFound(packageToScan);
        }

        Map<Boolean, List<Class<?>>> filteredBoundedContexts = set.stream()
                .collect(Collectors.partitioningBy(bc -> "package-info".equals(bc.getSimpleName())));

        if (filteredBoundedContexts.get(false) != null && !filteredBoundedContexts.get(false).isEmpty()) {
            throw new WrongUsageOfBoundedContext(filteredBoundedContexts.get(false));
        }
        return filteredBoundedContexts.get(true).stream()
                .map(bc -> BoundedContext.builder()
                        .name(bc.getPackageName())
                        .description(bc.getAnnotation(io.candydoc.domain.annotations.BoundedContext.class).description())
                        .build())
                .collect(Collectors.toList());
    }

    public void checkParameters(GenerateDocumentation command) throws DocumentationGenerationFailed {
        if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
            throw new DocumentationGenerationFailed("Missing parameters for 'packageToScan'. Check your pom configuration.");
        }
    }

}
