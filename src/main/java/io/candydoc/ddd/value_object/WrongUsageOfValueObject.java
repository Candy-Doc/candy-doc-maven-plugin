package io.candydoc.ddd.value_object;

import io.candydoc.ddd.model.ExtractionException;
import java.util.List;
import lombok.Getter;

// Todo: Check why never used ?
public class WrongUsageOfValueObject extends ExtractionException {
  @Getter private final List<Class<?>> wrongClasses;

  public WrongUsageOfValueObject(List<Class<?>> wrongClasses) {
    super("Value object should use primitive types only: " + wrongClasses);
    this.wrongClasses = wrongClasses;
  }
}
