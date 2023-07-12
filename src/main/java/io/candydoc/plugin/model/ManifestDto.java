package io.candydoc.plugin.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
public final class ManifestDto {

  private List<String> files;

  public static ManifestDto from(List<String> files) {
    return ManifestDto.builder().files(files).build();
  }
}
