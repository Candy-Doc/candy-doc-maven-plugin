package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.DomainModel;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.UbiquitousLanguage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ExtractDomainModelsUseCase {

    private final DomainModelFinder domainModelFinder;
    private final UbiquitousLanguageRepository ubiquitousLanguageRepository;
  
    public void checkParameters(ExtractDomainModels command) throws ExtractionException {
        if (command.packagesToScan().isEmpty()) {
            throw new PackageToScanMissing(
                "Missing parameters for 'packageToScan'. Check your pom configuration.");
        }
        if (command.packagesToScan().stream().anyMatch(String::isBlank)) {
            throw new PackageToScanMissing(
                "Blank packageToScan not allowed for 'packagesToScan'. Check your pom configuration");
        }
    }

    public void execute(ExtractDomainModels command) throws ExtractionException {
        checkParameters(command);

        Set<DomainModel> foundDomainModels = command.packagesToScan().stream()
            .map(PackageName::of)
            .map(domainModelFinder::findDomainModels)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());

        if (foundDomainModels.isEmpty()) {
            log.info("No domain model has been found in packages: {}", command.packagesToScan());
        } else {
            Set<UbiquitousLanguage> ubiquitousLanguages = UbiquitousLanguage.fromDomainModels(foundDomainModels);
            ubiquitousLanguageRepository.saveAll(ubiquitousLanguages);
            log.info("Domain models has been saved.");
        }
    }
}
