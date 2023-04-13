package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.*;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.tactical_pattern.*;
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
public class OneValidBoundedContextTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_ubiquitous_language_from_bounded_context() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.one_valid_bounded_context")
            .build());

    // then
    verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

    assertThat(ubiquitousLanguagesCaptor.getValue()).hasSize(1);

    assertThat(ubiquitousLanguagesCaptor.getValue())
        .first()
        .isInstanceOf(UbiquitousLanguage.BoundedVocabulary.class)
        .satisfies(
            ubiquitousLanguage -> {
              UbiquitousLanguage.BoundedVocabulary vocabulary =
                  (UbiquitousLanguage.BoundedVocabulary) ubiquitousLanguage;

              assertThat(vocabulary.boundedContext())
                  .isEqualTo(
                      BoundedContext.builder()
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.package-info"))
                          .simpleName(SimpleName.of("Bounded context one"))
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.one_valid_bounded_context"))
                          .description(Description.of("One valid bounded context"))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.AGGREGATE)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_COMMAND)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_EVENT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.ENTITY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
                                  .build())
                          .build());

              assertThat(vocabulary.domainModels())
                  .containsExactlyInAnyOrder(
                      Aggregate.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.AggregateOne"))
                          .simpleName(SimpleName.of("AggregateOne"))
                          .description(Description.of(""))
                          .build(),
                      DomainCommand.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainCommandOne"))
                          .simpleName(SimpleName.of("DomainCommandOne"))
                          .description(Description.of(""))
                          .build(),
                      DomainEvent.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.DomainEventOne"))
                          .simpleName(SimpleName.of("DomainEventOne"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.ENTITY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
                                  .build())
                          .build(),
                      Entity.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.EntityOne"))
                          .simpleName(SimpleName.of("EntityOne"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.one_valid_bounded_context.feature_A.ValueObjectOne"))
                          .simpleName(SimpleName.of("ValueObjectOne"))
                          .description(Description.of(""))
                          .build());
            });
  }
}
