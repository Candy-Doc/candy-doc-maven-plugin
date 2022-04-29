package io.candydoc.ddd.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Interaction {
  @NonNull private final CanonicalName canonicalName;

  public CanonicalName canonicalName() {
    return canonicalName;
  }

  public static Interaction with(String canonicalName) {
    return new Interaction(CanonicalName.of(canonicalName));
  }
}
