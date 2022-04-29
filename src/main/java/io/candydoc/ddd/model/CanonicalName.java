package io.candydoc.ddd.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class CanonicalName {
  @NonNull private final String value;

  public String value() {
    return value;
  }

  public static CanonicalName of(String value) {
    return new CanonicalName(value);
  }
}
