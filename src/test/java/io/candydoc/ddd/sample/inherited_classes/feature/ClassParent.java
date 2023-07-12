package io.candydoc.ddd.sample.inherited_classes.feature;

import io.candydoc.ddd.annotations.Aggregate;

@Aggregate
public abstract class ClassParent {

  private ValueObjectFromParent valueObjectFromParent;

  public ClassParent(ValueObjectFromParent valueObjectFromParent) {
    this.valueObjectFromParent = valueObjectFromParent;
  }
}
