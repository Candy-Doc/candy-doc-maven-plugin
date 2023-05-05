package io.candydoc.ddd.extract_domain_vocabulary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.model.Description;
import io.candydoc.ddd.model.FullName;
import io.candydoc.ddd.model.PackageName;
import io.candydoc.ddd.model.SimpleName;
import io.candydoc.ddd.model.UbiquitousLanguage;
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
public class AnonymousModelsTest {

  @Spy DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  @Mock UbiquitousLanguageRepository ubiquitousLanguageRepository;
  @InjectMocks ExtractDomainModelsUseCase useCase;
  @Captor ArgumentCaptor<Set<UbiquitousLanguage>> ubiquitousLanguagesCaptor;

  @Test
  public void extract_domain_models_from_anonymous_classes() {
    // when
    useCase.execute(
        ExtractDomainModels.builder()
            .packageToScan("io.candydoc.ddd.sample.anonymous_sample")
            .build());

    // then
    verify(ubiquitousLanguageRepository).saveAll(ubiquitousLanguagesCaptor.capture());

    assertThat(ubiquitousLanguagesCaptor.getValue()).hasSize(1);

    assertThat(ubiquitousLanguagesCaptor.getValue())
        .first()
        .satisfies(
            ubiquitousLanguage -> {
              assertThat(ubiquitousLanguage.domainModels())
                  .contains(
                      ValueObject.builder()
                          .packageName(PackageName.of("io.candydoc.ddd.sample.anonymous_sample"))
                          .fullName(
                              FullName.of("io.candydoc.ddd.sample.anonymous_sample.ParentModel$1"))
                          .simpleName(SimpleName.of("ParentModel$1"))
                          .description(Description.of(""))
                          .build(),
                      ValueObject.builder()
                          .packageName(PackageName.of("io.candydoc.ddd.sample.anonymous_sample"))
                          .fullName(
                              FullName.of("io.candydoc.ddd.sample.anonymous_sample.ParentModel$2"))
                          .simpleName(SimpleName.of("ParentModel$2"))
                          .description(Description.of(""))
                          .dependency(
                              Dependency.builder()
                                  .pattern(TacticalPattern.VALUE_OBJECT)
                                  .name(
                                      FullName.of(
                                          "io.candydoc.ddd.sample.anonymous_sample.ParentModel"))
                                  .build())
                          .build());
            });
  }
}
