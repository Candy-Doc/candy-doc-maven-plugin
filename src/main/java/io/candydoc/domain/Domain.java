package io.candydoc.domain;

import io.candydoc.domain.command.ExtractDDDConcept;
import io.candydoc.domain.events.*;
import io.candydoc.domain.exceptions.*;
import io.candydoc.domain.extractor.DDDConceptExtractor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Value
@RequiredArgsConstructor
@Slf4j
public class Domain {

    SaveDocumentationPort saveDocumentationPort;
    Logger logger = Logger.getLogger("logger");

    public void checkParameters(ExtractDDDConcept command) throws DocumentationGenerationFailed {
        if (command.getPackagesToScan() == null || command.getPackagesToScan().isEmpty()) {
            throw new DocumentationGenerationFailed("Missing parameters for 'packageToScan'. Check your pom configuration.");
        }
    }

   public void generateDocumentation(ExtractDDDConcept command) throws IOException, DomainException {
        DDDConceptExtractor DDDConceptExtractor = new DDDConceptExtractor();
        checkParameters(command);
        List<DomainEvent> domainEvents = DDDConceptExtractor.extract(command);
        saveDocumentationPort.save(domainEvents);
        logger.info("Documentation generation has succeeded.");
    }
}