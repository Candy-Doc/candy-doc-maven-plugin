package io.candydoc.ddd.sample.inherited_classes.feature;

import io.candydoc.ddd.annotations.Aggregate;

@Aggregate
public class ClassGrandChild extends ClassChild {
  private ValueObjectFromGrandChild valueObjectFromGrandChild;

  public ClassGrandChild(
      ValueObjectFromParent v1, ValueObjectFromChild v2, ValueObjectFromGrandChild v3) {
    super(v1, v2);
    this.valueObjectFromGrandChild = v3;
  }
}
