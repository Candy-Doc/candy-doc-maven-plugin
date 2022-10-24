package io.candydoc.ddd.extended_language.core_concept;

import io.candydoc.ddd.annotations.CoreConcept;
import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.FoundDomainObjects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindCoreConceptTest {

    static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
    static FoundDomainObjects foundDomainObjects;

    @BeforeAll
    public static void setUp() {
        foundDomainObjects = domainVocabularyFinder.findDomainObjects(List.of("io.candydoc.ddd.extended_language.core_concept"));
    }

    @Test
    public void find_core_concepts_from_classes() {
        assertThat(foundDomainObjects.toDomainModels())
            .filteredOn(domainModel -> "io.candydoc.ddd.extended_language.core_concept.FindCoreConceptTest.AggregateOne".equals(domainModel.canonicalName().value()))
            .first()
            .satisfies(domainModel -> assertThat(domainModel.coreConcept()).isFalse());

        assertThat(foundDomainObjects.toDomainModels())
            .filteredOn(domainModel -> "io.candydoc.ddd.extended_language.core_concept.FindCoreConceptTest.ValueObjectOne".equals(domainModel.canonicalName().value()))
            .first()
            .satisfies(domainModel -> assertThat(domainModel.coreConcept()).isTrue());
    }

    @CoreConcept
    @io.candydoc.ddd.annotations.ValueObject
    public static class ValueObjectOne {
    }

    @io.candydoc.ddd.annotations.Aggregate
    public static class AggregateOne {
    }

}
