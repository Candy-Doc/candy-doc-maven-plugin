package io.candydoc.domain.exceptions;

import java.util.List;
import lombok.Getter;

public class WrongUsageOfValueObject extends DomainException {
  @Getter private final List<Class<?>> wrongClasses;

  public WrongUsageOfValueObject(List<Class<?>> wrongClasses) {
    super("Value object should use primitive types only: " + wrongClasses);
    this.wrongClasses = wrongClasses;
  }
}
