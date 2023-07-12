package io.candydoc.ddd.sample.inherited_classes.feature;

import io.candydoc.ddd.annotations.Aggregate;

@Aggregate
public abstract class ClassChild extends ClassParent {

  private ValueObjectFromChild valueObjectFromChild;

  public ClassChild(ValueObjectFromParent v1, ValueObjectFromChild v2) {
    super(v1);
    this.valueObjectFromChild = v2;
  }
}
