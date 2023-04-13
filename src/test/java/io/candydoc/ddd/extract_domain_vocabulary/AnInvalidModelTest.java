package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnInvalidModelTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;

  @Test
  void an_object_can_not_be_defined_by_multiple_ddd_patterns() {
    // when
    ThrowableAssert.ThrowingCallable callable =
        () ->
            useCase.execute(
                ExtractDomainModels.builder()
                    .packageToScan("io.candydoc.ddd.sample.invalid_models")
                    .build());

    assertThatThrownBy(callable)
        .isInstanceOf(ExtractionException.class)
        .isInstanceOf(WrongUseOfDDDPatterns.class)
        .hasMessageContaining(
            "Domain object 'io.candydoc.ddd.sample.invalid_models.InvalidModelOne' matches more"
                + " than one DDD pattern: [AGGREGATE, DOMAIN_COMMAND]")
        .hasMessageContaining(
            "Domain object 'io.candydoc.ddd.sample.invalid_models.InvalidModelTwo' matches more"
                + " than one DDD pattern: [DOMAIN_EVENT, VALUE_OBJECT]");
  }
}
