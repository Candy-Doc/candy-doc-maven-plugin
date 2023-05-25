package io.candydoc.plugin.model;

import java.util.Collection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@Getter
public class UbiquitousLanguageWithRelationsDto extends UbiquitousLanguageDto {

  private final Collection<RelationDto> relations;
}
