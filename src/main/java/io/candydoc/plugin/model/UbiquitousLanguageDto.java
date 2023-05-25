package io.candydoc.plugin.model;

import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@Getter
public class UbiquitousLanguageDto {

  private final String type;
  private final String name;
  private final String description;
  private final Map<String, DomainModelDto> domainModels;

  public static UbiquitousLanguageDto from(UbiquitousLanguage ubiquitousLanguage) {
    Map<String, DomainModelDto> domainModelPerFullName =
        ubiquitousLanguage.domainModels().stream()
            .map(
                domainModel ->
                    Map.entry(domainModel.fullName().value(), DomainModelDto.from(domainModel)))
            .sorted(Map.Entry.comparingByKey())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, TreeMap::new));

    return ubiquitousLanguage.accept(
        new UbiquitousLanguage.Visitor<>() {
          @Override
          public UbiquitousLanguageDto handle(UbiquitousLanguage.BoundedVocabulary vocabulary) {
            BoundedContext boundedContext = vocabulary.boundedContext();

            return UbiquitousLanguageDto.builder()
                .type("BOUNDED_CONTEXT")
                .name(boundedContext.simpleName().value())
                .description(boundedContext.description().value())
                .domainModels(domainModelPerFullName)
                .build();
          }

          @Override
          public UbiquitousLanguageDto handle(UbiquitousLanguage.SharedVocabulary vocabulary) {
            SharedKernel sharedKernel = vocabulary.sharedKernel();

            return UbiquitousLanguageWithRelationsDto.builder()
                .type("SHARED_KERNEL")
                .name(sharedKernel.simpleName().value())
                .description(sharedKernel.description().value())
                .relations(
                    sharedKernel.relations().stream()
                        .map(relation -> RelationDto.builder().relatedTo(relation.values()).build())
                        .sorted(Comparator.comparing(RelationDto::getRelatedTo))
                        .collect(Collectors.toUnmodifiableList()))
                .domainModels(domainModelPerFullName)
                .build();
          }

          @Override
          public UbiquitousLanguageDto handle(
              UbiquitousLanguage.OrphanBoundedVocabulary vocabulary) {
            return UbiquitousLanguageDto.builder()
                .type("ORPHAN_VOCABULARY")
                .name("Orphan vocabulary")
                .description("These models are not under a bounded context nor a shared kernel")
                .domainModels(domainModelPerFullName)
                .build();
          }
        });
  }
}
