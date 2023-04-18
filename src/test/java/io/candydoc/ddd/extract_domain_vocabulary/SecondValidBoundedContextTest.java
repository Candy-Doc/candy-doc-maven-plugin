package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.CanonicalName;
import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.DomainWarning;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.DomainEntity;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.DomainService;
import io.candydoc.ddd.tactical_pattern.Factory;
import io.candydoc.ddd.tactical_pattern.Repository;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
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
public class SecondValidBoundedContextTest {
  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_ubiquitous_language_from_bounded_context() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.second_valid_bounded_context")
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
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.package-info"))
                          .simpleName(SimpleName.of("Bounded context two"))
                          .packageName(
                              PackageName.of("io.candydoc.ddd.sample.second_valid_bounded_context"))
                          .description(Description.of("Second valid bounded context"))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.AGGREGATE)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Aggregate_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_COMMAND)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainCommand_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_EVENT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainEvent_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z1"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z2"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_ENTITY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Entity_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.REPOSITORY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Repository_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.FACTORY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Factory_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_SERVICE)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainService_Z"))
                                  .build())
                          .build());

              assertThat(vocabulary.domainModels())
                  .containsExactlyInAnyOrder(
                      Aggregate.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Aggregate_Z"))
                          .simpleName(SimpleName.of("Aggregate_Z"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_ENTITY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Entity_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z1"))
                                  .build())
                          .build(),
                      DomainCommand.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainCommand_Z"))
                          .simpleName(SimpleName.of("DomainCommand_Z"))
                          .description(Description.of(""))
                          .build(),
                      DomainEvent.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainEvent_Z"))
                          .simpleName(SimpleName.of("DomainEvent_Z"))
                          .description(Description.of(""))
                          .build(),
                      DomainEntity.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Entity_Z"))
                          .simpleName(SimpleName.of("Entity_Z"))
                          .description(Description.of(""))
                          .warning(DomainWarning.NO_ID)
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z2"))
                                  .build())
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z1"))
                          .simpleName(SimpleName.of("ValueObject_Z1"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.ValueObject_Z2"))
                          .simpleName(SimpleName.of("ValueObject_Z2"))
                          .description(Description.of(""))
                          .build(),
                      Repository.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Repository_Z"))
                          .simpleName(SimpleName.of("Repository_Z"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.AGGREGATE)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Aggregate_Z"))
                                  .build())
                          .build(),
                      Factory.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Factory_Z"))
                          .simpleName(SimpleName.of("Factory_Z"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.REPOSITORY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Repository_Z"))
                                  .build())
                          .build(),
                      DomainService.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z"))
                          .canonicalName(
                              CanonicalName.of(
                                  "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainService_Z"))
                          .simpleName(SimpleName.of("DomainService_Z"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.REPOSITORY)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.Repository_Z"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.DOMAIN_EVENT)
                                  .name(
                                      CanonicalName.of(
                                          "io.candydoc.ddd.sample.second_valid_bounded_context.feature_Z.DomainEvent_Z"))
                                  .build())
                          .build());
            });
  }
}
