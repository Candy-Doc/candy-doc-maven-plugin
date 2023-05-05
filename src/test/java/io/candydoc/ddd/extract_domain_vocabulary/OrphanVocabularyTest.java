package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.ValueObject;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrphanVocabularyTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor private ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_ubiquitous_language_that_is_not_in_a_bounded_context_nor_a_shared_kernel() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.just_a_package")
            .build());

    // then
    verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

    assertThat(ubiquitousLanguagesCaptor.getValue()).hasSize(1);

    assertThat(ubiquitousLanguagesCaptor.getValue())
        .first()
        .isInstanceOf(UbiquitousLanguage.OrphanBoundedVocabulary.class)
        .satisfies(
            ubiquitousLanguage -> {
              UbiquitousLanguage.OrphanBoundedVocabulary vocabulary =
                  (UbiquitousLanguage.OrphanBoundedVocabulary) ubiquitousLanguage;

              assertThat(vocabulary.domainModels())
                  .containsExactlyInAnyOrder(
                      Aggregate.builder()
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.just_a_package.feature_A.AggregateOne"))
                          .simpleName(SimpleName.of("AggregateOne"))
                          .description(Description.of(""))
                          .build(),
                      DomainCommand.builder()
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.just_a_package.feature_A.DomainCommandOne"))
                          .simpleName(SimpleName.of("DomainCommandOne"))
                          .description(Description.of(""))
                          .build(),
                      DomainEvent.builder()
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.just_a_package.feature_A.DomainEventOne"))
                          .simpleName(SimpleName.of("DomainEventOne"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.just_a_package.feature_A"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.just_a_package.feature_A.ValueObjectOne"))
                          .simpleName(SimpleName.of("ValueObjectOne"))
                          .description(Description.of(""))
                          .build());
            });
  }
}
