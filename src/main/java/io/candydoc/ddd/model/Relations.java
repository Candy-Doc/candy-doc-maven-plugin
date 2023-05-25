package io.candydoc.ddd.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Relations {
  private final String value;

  public String values() {
    return value;
  }

  public static Relations of(String value) {
    return new Relations(value);
  }
}
