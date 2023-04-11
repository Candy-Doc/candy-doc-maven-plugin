package io.candydoc.ddd.tactical_patterns.domain_event;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.DomainEvent;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindDomainEventTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.domain_event"));
  }

  @Test
  public void find_domain_event_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainEvent.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest$DomainEventOne"))
                .simpleName(SimpleName.of("DomainEventOne"))
                .description(Description.of("Describe the domain event here"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            FullName.of(
                                "io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest$ValueObjectOne"))
                        .build())
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            DomainEvent.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.domain_event"))
                .fullName(
                    FullName.of(
                        "io.candydoc.ddd.tactical_patterns.domain_event.FindDomainEventTest$DomainEventTwo"))
                .simpleName(SimpleName.of("DomainEventTwo"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.DomainEvent(description = "Describe the domain event here")
  public static class DomainEventOne {
    private ValueObjectOne valueObjectOne;
  }

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectOne {}

  @io.candydoc.ddd.annotations.DomainEvent
  public static class DomainEventTwo {}
}
