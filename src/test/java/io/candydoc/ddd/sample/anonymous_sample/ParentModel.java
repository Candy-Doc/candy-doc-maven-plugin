package io.candydoc.ddd.sample.anonymous_sample;

import io.candydoc.ddd.annotations.ValueObject;

@ValueObject
public class ParentModel {

  private static final AbstractModel staticAnonymousModel = new AbstractModel() {};

  private final AbstractModel anonymousModel = new AbstractModel() {};
}
