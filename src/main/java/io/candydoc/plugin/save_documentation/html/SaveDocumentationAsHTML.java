package io.candydoc.plugin.save_documentation.html;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DocumentationGenerationFailed;
import io.candydoc.ddd.extract_ddd_concepts.SaveDocumentationPort;
import io.candydoc.plugin.model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

@RequiredArgsConstructor
public class SaveDocumentationAsHTML implements SaveDocumentationPort {

  private static final Path HTML_DESTINATION_FOLDER = Paths.get("target", "candy-doc", "html");
  private static final Path HTML_BASE_FOLDER = HTML_DESTINATION_FOLDER.toAbsolutePath();
  private final FreemarkerEngine templateEngine;

  @Override
  public void save(List<Event> domainEvents) throws IOException {
    FileUtils.deleteDirectory(HTML_DESTINATION_FOLDER.toFile());
    Files.createDirectories(HTML_DESTINATION_FOLDER);
    List<DomainContextDto> domainContexts = DomainContextDtoMapper.map(domainEvents);
    generatePagesForBoundedContexts(
        domainContexts.stream()
            .filter(domainContextDto -> domainContextDto instanceof BoundedContextDto)
            .map(BoundedContextDto.class::cast)
            .collect(Collectors.toUnmodifiableList()));
    generateStyle();
  }

  private void generatePagesForBoundedContexts(List<BoundedContextDto> boundedContexts) {
    Map<String, Object> model = new HashMap<>();
    model.put("boundedContexts", boundedContexts);
    Path fileDestination = HTML_DESTINATION_FOLDER.resolve("index.html");
    templateEngine.generatePage("index", fileDestination, model);
    boundedContexts.forEach(
        boundedContext -> {
          try {
            generateBoundedContextPage(boundedContext, boundedContexts);
          } catch (IOException e) {
            throw new DocumentationGenerationFailed(e.getMessage());
          }
        });
  }

  private void generateBoundedContextPage(
      BoundedContextDto boundedContext, List<BoundedContextDto> boundedContexts)
      throws IOException {
    Path boundedContextDirectory = HTML_DESTINATION_FOLDER.resolve(boundedContext.getPackageName());
    Files.createDirectories(boundedContextDirectory);
    Map<String, Object> model = new HashMap<>();
    model.put("boundedContext", boundedContext);
    model.put("boundedContexts", boundedContexts);
    model.put("baseFolder", HTML_BASE_FOLDER);
    Path fileDestination =
        boundedContextDirectory.resolve(boundedContext.getPackageName() + ".html");
    templateEngine.generatePage("bounded_context", fileDestination, model);
    Arrays.stream(ConceptType.values())
        .map(boundedContext::getConcepts)
        .flatMap(Collection::stream)
        .forEach(
            concepts ->
                generatePage(concepts, boundedContextDirectory, boundedContext, boundedContexts));
  }

  private void generatePage(
      ConceptDto concept,
      Path boundedContextDirectory,
      BoundedContextDto boundedContext,
      List<BoundedContextDto> boundedContexts) {
    Map<String, Object> model = new HashMap<>();
    model.put("baseFolder", HTML_BASE_FOLDER);
    model.put("boundedContext", boundedContext);
    model.put("boundedContexts", boundedContexts);
    model.put("concept", concept);
    Path fileDestination = boundedContextDirectory.resolve(concept.getCanonicalName() + ".html");
    templateEngine.generatePage("concept_page", fileDestination, model);
  }

  private void generateStyle() {
    Path fileDestination = HTML_DESTINATION_FOLDER.resolve("style.css");
    templateEngine.generateStyle(fileDestination);
  }
}
