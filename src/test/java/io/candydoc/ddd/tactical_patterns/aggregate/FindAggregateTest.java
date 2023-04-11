package io.candydoc.ddd.tactical_patterns.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import io.candydoc.ddd.extract_domain_vocabulary.DomainVocabularyFinder;
import io.candydoc.ddd.extract_domain_vocabulary.ReflectionsDomainVocabularyFinder;
import io.candydoc.ddd.model.*;
import io.candydoc.ddd.tactical_pattern.Aggregate;
import io.candydoc.ddd.tactical_pattern.TacticalPattern;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindAggregateTest {

  static DomainVocabularyFinder domainVocabularyFinder = new ReflectionsDomainVocabularyFinder();
  static FoundDomainObjects foundDomainObjects;

  @BeforeAll
  public static void setUp() {
    foundDomainObjects =
        domainVocabularyFinder.findDomainObjects(
            List.of("io.candydoc.ddd.tactical_patterns.aggregate"));
  }

  @Test
  public void find_aggregate_from_classes() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.AggregateOne"))
                .simpleName(SimpleName.of("My aggregate"))
                .description(Description.of("Describe the aggregate here"))
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.VALUE_OBJECT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.ValueObjectOne"))
                        .build())
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.DOMAIN_COMMAND)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.DomainCommandOne"))
                        .build())
                .dependency(
                    Dependency.builder()
                        .pattern(TacticalPattern.DOMAIN_EVENT)
                        .name(
                            CanonicalName.of(
                                "io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.DomainEventOne"))
                        .build())
                .build());
  }

  @Test
  public void attributes_not_provided() {
    assertThat(foundDomainObjects.toDomainModels())
        .contains(
            Aggregate.builder()
                .packageName(PackageName.of("io.candydoc.ddd.tactical_patterns.aggregate"))
                .canonicalName(
                    CanonicalName.of(
                        "io.candydoc.ddd.tactical_patterns.aggregate.FindAggregateTest.AggregateTwo"))
                .simpleName(SimpleName.of("AggregateTwo"))
                .description(Description.of(""))
                .build());
  }

  @io.candydoc.ddd.annotations.Aggregate(
      name = "My aggregate",
      description = "Describe the aggregate here")
  public static class AggregateOne {
    private ValueObjectOne valueObjectOne;

    public DomainEventOne handle(DomainCommandOne command) {
      return new DomainEventOne();
    }

    public void execute(String firstElement, String secondElement) {}
  }

  @io.candydoc.ddd.annotations.DomainCommand
  public static class DomainCommandOne {}

  @io.candydoc.ddd.annotations.DomainEvent
  public static class DomainEventOne {}

  @io.candydoc.ddd.annotations.ValueObject
  public static class ValueObjectOne {}

  @io.candydoc.ddd.annotations.Aggregate
  public static class AggregateTwo {}
}
