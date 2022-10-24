package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.model.FoundDomainObjects;

import java.util.Collection;

public interface DomainVocabularyFinder {

    FoundDomainObjects findDomainObjects(Collection<String> packagesToScan);

}
