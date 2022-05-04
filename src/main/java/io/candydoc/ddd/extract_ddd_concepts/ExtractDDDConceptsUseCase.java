package io.candydoc.ddd.extract_ddd_concepts;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.model.ExtractionException;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtractDDDConceptsUseCase {

  private final SaveDocumentationPort saveDocumentationPort;
  private final DDDConceptsExtractionService DDDConceptsExtractionService;

  public ExtractDDDConceptsUseCase(
      DDDConceptsExtractionService dddConceptsExtractionService,
      SaveDocumentationPort saveDocumentationPort) {
    this.DDDConceptsExtractionService = dddConceptsExtractionService;
    this.saveDocumentationPort = saveDocumentationPort;
  }

  public void checkParameters(ExtractDDDConcepts command) throws ExtractionException {
    if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
      throw new PackageToScanMissing(
          "Missing parameters for 'packageToScan'. Check your pom configuration.");
    }
  }

  public void execute(ExtractDDDConcepts command) throws IOException, ExtractionException {
    checkParameters(command);
    List<Event> domainEvents = DDDConceptsExtractionService.extract(command);
    saveDocumentationPort.save(domainEvents);
    log.info("Documentation generation has succeeded.");
  }
}
