package io.candydoc.ddd;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SubdomainType {
  CoreDomain(0),
  GenericSubdomain(1),
  SupportingSubdomain(2);

  private final int type;

  SubdomainType(int type) {
    this.type = type;
  }

  @JsonValue
  public int getType() {
    return this.type;
  }
}
