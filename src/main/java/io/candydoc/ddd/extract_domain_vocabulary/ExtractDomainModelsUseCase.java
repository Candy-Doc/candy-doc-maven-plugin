package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ExtractDomainModelsUseCase {

    private final DomainVocabularyFinder domainVocabularyFinder;
    private final UbiquitousLanguageRepository ubiquitousLanguageRepository;
  
    public void checkParameters(ExtractDomainModels command) throws ExtractionException {
        if (command.packagesToScan().isEmpty()) {
            throw new PackageToScanMissing(
                "At least one 'packageToScan' must be provided. Check your pom configuration.");
        }
        if (command.packagesToScan().stream().anyMatch(String::isBlank)) {
            throw new PackageToScanMissing(
                "Blank packageToScan not allowed for 'packagesToScan'. Check your pom configuration.");
        }
    }

    public void execute(ExtractDomainModels command) throws ExtractionException {
        checkParameters(command);

        FoundDomainObjects foundDomainObjects = domainVocabularyFinder.findDomainObjects(command.packagesToScan());

        if (foundDomainObjects.isEmpty()) {
            log.info("No domain model has been found in packages: {}.", command.packagesToScan());
        } else {
            Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainObjects(foundDomainObjects);
            ubiquitousLanguageRepository.saveAll(ubiquitousLanguages);
        }
        log.info("Domain models extraction done.");
    }
}
