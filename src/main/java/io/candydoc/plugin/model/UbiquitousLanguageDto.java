package io.candydoc.plugin.model;

import io.candydoc.ddd.model.UbiquitousLanguage;
import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Jacksonized
@Builder
@Getter
public final class UbiquitousLanguageDto {

    private final String type;
    private final String name;
    private final String description;
    private final Map<String, DomainModelDto> domainModels;

    public static UbiquitousLanguageDto from(UbiquitousLanguage ubiquitousLanguage) {
        Map<String, DomainModelDto> domainModelPerCanonicalName = ubiquitousLanguage.domainModels().stream()
                .map(domainModel -> Map.entry(domainModel.canonicalName().value(), DomainModelDto.from(domainModel)))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, TreeMap::new));

        return ubiquitousLanguage.accept(new UbiquitousLanguage.Visitor<>() {
            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.BoundedVocabulary vocabulary) {
                BoundedContext boundedContext = vocabulary.boundedContext();

                return UbiquitousLanguageDto.builder()
                        .type("BOUNDED_CONTEXT")
                        .name(boundedContext.simpleName().value())
                        .description(boundedContext.description().value())
                        .domainModels(domainModelPerCanonicalName)
                        .build();
            }

            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.SharedVocabulary vocabulary) {
                SharedKernel sharedKernel = vocabulary.sharedKernel();

                return UbiquitousLanguageDto.builder()
                        .type("SHARED_KERNEL")
                        .name(sharedKernel.simpleName().value())
                        .description(sharedKernel.description().value())
                        .domainModels(domainModelPerCanonicalName)
                        .build();
            }

            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.OrphanBoundedVocabulary vocabulary) {
                return UbiquitousLanguageDto.builder()
                        .type("ORPHAN_VOCABULARY")
                        .name("Orphan vocabulary")
                        .description("These models are not under a bounded context nor a shared kernel")
                        .domainModels(domainModelPerCanonicalName)
                        .build();
            }
        });
    }

}
