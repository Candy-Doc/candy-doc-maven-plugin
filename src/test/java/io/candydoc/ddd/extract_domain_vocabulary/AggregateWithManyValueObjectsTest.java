package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.tactical_pattern.Aggregate;
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
public class AggregateWithManyValueObjectsTest {
  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_ubiquitous_language_from_bounded_context() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.aggregate_with_many_value_objects")
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
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.package-info"))
                          .simpleName(SimpleName.of("Bounded context"))
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects"))
                          .description(Description.of("This is a bounded context"))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.AGGREGATE)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.Aggregate"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObject"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObjectTwo"))
                                  .build())
                          .build());

              assertThat(vocabulary.domainModels())
                  .containsExactlyInAnyOrder(
                      Aggregate.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.Aggregate"))
                          .simpleName(SimpleName.of("Aggregate"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObject"))
                                  .build())
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObjectTwo"))
                                  .build())
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObject"))
                          .simpleName(SimpleName.of("ValueObject"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(
                              PackageName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature"))
                          .fullName(
                              FullName.of(
                                  "io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature.ValueObjectTwo"))
                          .simpleName(SimpleName.of("ValueObjectTwo"))
                          .description(Description.of(""))
                          .build());
            });
  }
}
