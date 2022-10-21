package io.candydoc.ddd.extract_domain_vocabulary;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class AnInvalidModelTest {


    DomainVocabularyFinder domainVocabularyFinder;
    UbiquitousLanguageRepository ubiquitousLanguageRepository;
    ExtractDomainModelsUseCase useCase;

    @BeforeEach
    public void setUp() {
        this.domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
        this.ubiquitousLanguageRepository = mock(UbiquitousLanguageRepository.class);
        this.useCase = new ExtractDomainModelsUseCase(domainVocabularyFinder, ubiquitousLanguageRepository);
    }

    @Test
    void an_object_can_not_be_defined_by_multiple_ddd_patterns() {
        // when
        ThrowableAssert.ThrowingCallable callable = () -> useCase.execute(ExtractDomainModels.builder()
                .packageToScan("io.candydoc.ddd.sample.invalid_models")
                .build());

        assertThatThrownBy(callable)
                .isInstanceOf(ExtractionException.class)
                .isInstanceOf(WrongUseOfDDDPatterns.class)
                .hasMessageContaining("Domain object 'io.candydoc.ddd.sample.invalid_models.InvalidModelOne' matches more than one DDD pattern: [AGGREGATE, DOMAIN_COMMAND]")
                .hasMessageContaining("Domain object 'io.candydoc.ddd.sample.invalid_models.InvalidModelTwo' matches more than one DDD pattern: [DOMAIN_EVENT, VALUE_OBJECT]");
    }

}
