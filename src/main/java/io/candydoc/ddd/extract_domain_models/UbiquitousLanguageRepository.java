package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.UbiquitousLanguage;

import java.util.Set;

public interface UbiquitousLanguageRepository {
    void saveAll(Set<UbiquitousLanguage> ubiquitousLanguages);
}
