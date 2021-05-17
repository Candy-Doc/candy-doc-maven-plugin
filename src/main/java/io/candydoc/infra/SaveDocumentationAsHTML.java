package io.candydoc.infra;

import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.domain.events.DomainEvent;
import io.candydoc.domain.exceptions.DocumentationGenerationFailed;
import io.candydoc.infra.model.BoundedContextDto;
import io.candydoc.infra.model.ConceptDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class SaveDocumentationAsHTML implements SaveDocumentationPort {

    private static final Path HTML_DESTINATION_FOLDER = Paths.get("target", "candy-doc", "html");
    private static final Path HTML_BASE_FOLDER = HTML_DESTINATION_FOLDER.toAbsolutePath();
    private final TemplateEngine templateEngine;

    @Override
    public void save(List<DomainEvent> domainEvents) throws IOException {
        FileUtils.deleteDirectory(HTML_DESTINATION_FOLDER.toFile());
        Files.createDirectories(HTML_DESTINATION_FOLDER);
        BoundedContextDtoMapper mapper = new BoundedContextDtoMapper();
        List<BoundedContextDto> boundedContexts = mapper.map(domainEvents);
        generatePages(boundedContexts);
        generateStyle();
    }

    private void generatePages(List<BoundedContextDto> boundedContexts) {
        Map<String, Object> model = new HashMap<>();
        model.put("boundedContexts", boundedContexts);
        Path fileDestination = HTML_DESTINATION_FOLDER.resolve("index.html");
        templateEngine.generatePage("index", fileDestination, model);
        boundedContexts.forEach(boundedContext -> {
            try {
                generateBoundedContextPage(boundedContext, boundedContexts);
            } catch (IOException e) {
                throw new DocumentationGenerationFailed(e.getMessage());
            }
        });
    }

    private void generateBoundedContextPage(BoundedContextDto boundedContext, List<BoundedContextDto> boundedContexts) throws IOException {
        Path boundedContextDirectory = HTML_DESTINATION_FOLDER.resolve(boundedContext.getName());
        Files.createDirectories(boundedContextDirectory);
        Map<String, Object> model = new HashMap<>();
        model.put("boundedContext", boundedContext);
        model.put("navigation", generateNavigationFragment(boundedContexts));
        model.put("baseFolder", HTML_BASE_FOLDER);
        Path fileDestination = boundedContextDirectory.resolve(boundedContext.getName() + ".html");
        templateEngine.generatePage("bounded_context", fileDestination, model);
        boundedContext.getCoreConcepts().forEach(coreConcept ->
                generatePage(coreConcept, boundedContextDirectory, boundedContext, boundedContexts));
        boundedContext.getValueObjects().forEach(valueObject ->
                generatePage(valueObject, boundedContextDirectory, boundedContext, boundedContexts));
        boundedContext.getDomainEvents().forEach(domainEvent ->
                generatePage(domainEvent, boundedContextDirectory, boundedContext, boundedContexts));
        boundedContext.getDomainCommands().forEach(domainCommand ->
                generatePage(domainCommand, boundedContextDirectory, boundedContext, boundedContexts));
    }

    private void generatePage(ConceptDto concept, Path boundedContextDirectory, BoundedContextDto boundedContext, List<BoundedContextDto> boundedContexts) {
        Map<String, Object> model = new HashMap<>();
        model.put("concept", generateConceptFragment(concept, boundedContext));
        model.put("navigation", generateNavigationFragment(boundedContexts));
        model.put("baseFolder", HTML_BASE_FOLDER);
        Path fileDestination = boundedContextDirectory.resolve(concept.getFullName() + ".html");
        templateEngine.generatePage("concept_page", fileDestination, model);
    }

    private String generateConceptFragment(ConceptDto concept, BoundedContextDto boundedContext) {
        Map<String, Object> model = new HashMap<>();
        model.put("concept", concept);
        model.put("boundedContext", boundedContext);
        return templateEngine.generateFragment("concept", model);
    }

    private String generateNavigationFragment(List<BoundedContextDto> boundedContexts) {
        Map<String, Object> model = new HashMap<>();
        model.put("boundedContexts", boundedContexts);
        return templateEngine.generateFragment("navigation", model);
    }

    private void generateStyle() {
        Path fileDestination = HTML_DESTINATION_FOLDER.resolve("style.css");
        templateEngine.generateStyle(fileDestination);
    }
}