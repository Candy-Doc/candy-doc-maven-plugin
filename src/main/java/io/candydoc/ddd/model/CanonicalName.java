package io.candydoc.ddd.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class CanonicalName implements Comparable<CanonicalName> {
  @NonNull private final String value;

  public String value() {
    return value;
  }

  @Override
  public int compareTo(CanonicalName other) {
    return value.compareTo(other.value);
  }

  public static CanonicalName of(String value) {
    return new CanonicalName(value);
  }
}
