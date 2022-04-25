package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.model.ExtractionException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExtractDDDConceptsUseCase {

  private final SaveDocumentationPort saveDocumentationPort;
  private final DDDConceptFinder DDDConceptFinder;

  public void checkParameters(ExtractDDDConcepts command) throws DocumentationGenerationFailed {
    if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
      throw new DocumentationGenerationFailed(
          "Missing parameters for 'packageToScan'. Check your pom configuration.");
    }
  }

  public void execute(ExtractDDDConcepts command) throws IOException, ExtractionException {
    DDDConceptExtractor DDDConceptExtractor = new DDDConceptExtractor(DDDConceptFinder);
    checkParameters(command);
    List<Event> domainEvents = DDDConceptExtractor.extract(command);
    saveDocumentationPort.save(domainEvents);
    log.info("Documentation generation has succeeded.");
  }
}
