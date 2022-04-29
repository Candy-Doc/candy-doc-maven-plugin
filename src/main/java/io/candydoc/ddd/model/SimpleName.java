package io.candydoc.ddd.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class SimpleName {
  @NonNull private final String value;

  public String value() {
    return value;
  }

  public static SimpleName of(String value) {
    return new SimpleName(value);
  }
}
