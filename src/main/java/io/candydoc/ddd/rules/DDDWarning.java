package io.candydoc.ddd.rules;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class DDDWarning {
  private final String value;

  public String value() {
    return value;
  }

  public static DDDWarning of(String value) {
    return new DDDWarning(value);
  }
}
