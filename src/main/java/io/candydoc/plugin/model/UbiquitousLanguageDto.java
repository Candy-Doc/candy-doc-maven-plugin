package io.candydoc.plugin.model;

import io.candydoc.ddd.model.UbiquitousLanguage;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
public final class UbiquitousLanguageDto {

    private final String contextName;
    private final String description;
    private final boolean boundedContext;
    private final Set<DomainModelDto> domainModels;

    public static UbiquitousLanguageDto from(UbiquitousLanguage ubiquitousLanguage) {
        return ubiquitousLanguage.accept(new UbiquitousLanguage.Visitor<>() {
            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.BoundedVocabulary vocabulary) {
                return UbiquitousLanguageDto.builder()
                    .contextName(vocabulary.boundedContext().simpleName().value())
                    .build();
            }

            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.SharedVocabulary vocabulary) {
                return UbiquitousLanguageDto.builder()
                    .contextName("")
                    .build();
            }

            @Override
            public UbiquitousLanguageDto handle(UbiquitousLanguage.NotBoundedVocabulary vocabulary) {
                return UbiquitousLanguageDto.builder()
                    .contextName("")
                    .build();
            }
        });
    }

}
