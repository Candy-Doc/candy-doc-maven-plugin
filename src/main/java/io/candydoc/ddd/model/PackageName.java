package io.candydoc.ddd.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class PackageName {
  private final String value;

  public String value() {
    return value;
  }

  public static PackageName of(String value) {
    return new PackageName(value);
  }

  public boolean startsWith(String packageToScan) {
    return this.value.startsWith(packageToScan);
  }
}
