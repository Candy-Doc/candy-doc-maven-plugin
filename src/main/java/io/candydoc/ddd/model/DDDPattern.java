package io.candydoc.ddd.model;

import java.lang.annotation.Annotation;

public interface DDDPattern {

  String name();

  Class<? extends Annotation> relatedAnnotation();

  <T> T accept(Visitor<T> visitor);

  interface Visitor<T> {
    T boundedContext();

    T sharedKernel();

    T aggregate();

    T domainCommand();

    T domainEvent();

    T valueObject();

    T entity();

    T factory();
  }
}
