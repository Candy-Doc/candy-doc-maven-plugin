package io.candydoc.ddd.tactical_patterns.domain_command;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.DomainCommand;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindDomainCommandTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.domain_command"));
  }

  @Test
  public void find_domain_command_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainCommand.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_command"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_command.FindDomainCommandTest$DomainCommandOne"))
                .simpleName(SimpleName.of("FindDomainCommandTest$DomainCommandOne"))
                .description(Description.of("Describe the domain command here"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            FullName.of(
                                "io.candydoc.ddd.tactical_patterns.domain_command.FindDomainCommandTest$ValueObjectOne"))
                        .build())
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainCommand.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_command"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_command.FindDomainCommandTest$DomainCommandTwo"))
                .simpleName(SimpleName.of("FindDomainCommandTest$DomainCommandTwo"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.DomainCommand(description = "Describe the domain command here")
  public static class DomainCommandOne {
    private ValueObjectOne valueObjectOne;
  }

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectOne {}

  @io.candydoc.ddd.annotations.DomainCommand
  public static class DomainCommandTwo {}
}
