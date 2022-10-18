package io.candydoc.ddd.extract_domain_models;

import io.candydoc.ddd.model.UbiquitousLanguage;

import java.io.IOException;

public interface SaveUbiquitousLanguagePort {
    void save(UbiquitousLanguage ubiquitousLanguage) throws IOException;
}
