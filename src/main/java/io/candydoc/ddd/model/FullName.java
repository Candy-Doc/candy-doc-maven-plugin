package io.candydoc.ddd.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class FullName implements Comparable<FullName> {
  @NonNull private final String value;

  public String value() {
    return value;
  }

  @Override
  public int compareTo(FullName other) {
    return value.compareTo(other.value);
  }

  public static FullName of(String value) {
    return new FullName(value);
  }
}
