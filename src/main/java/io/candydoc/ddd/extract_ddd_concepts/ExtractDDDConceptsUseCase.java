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
    if (command.getPackagesToScan().isEmpty()) {
      throw new PackageToScanMissing(
          "Missing parameters for 'packageToScan'. Check your pom configuration.");
    }
    if (command.getPackagesToScan().stream().anyMatch(String::isBlank)) {
      throw new PackageToScanMissing(
          "Blank packageToScan not allowed for 'packagesToScan'. Check your pom configuration");
    }
  }

  public void execute(ExtractDDDConcepts command) throws IOException, ExtractionException {
    checkParameters(command);
    List<Event> domainEvents = DDDConceptsExtractionService.extract(command);
    if (domainEvents.isEmpty()) {
      throw new NoBoundedContextNorSharedKernelFound(command.getPackagesToScan());
    }
    saveDocumentationPort.save(domainEvents);
    log.info("Documentation generation has succeeded.");
  }
}
