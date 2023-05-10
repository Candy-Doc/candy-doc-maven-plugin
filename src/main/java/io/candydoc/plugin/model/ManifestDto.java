package io.candydoc.plugin.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
public final class ManifestDto {

  @Builder.Default private List<String> files = new ArrayList<>();

  public void add(String filePath) {
    files.add(filePath);
  }
}
