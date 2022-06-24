package io.candydoc.ddd.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Relation {
  private final String value;

  public String value() {
    return value;
  }

  public static Relation of(String value) {
    return new Relation(value);
  }
}
