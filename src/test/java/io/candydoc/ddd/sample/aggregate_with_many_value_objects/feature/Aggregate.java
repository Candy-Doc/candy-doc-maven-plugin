package io.candydoc.ddd.sample.aggregate_with_many_value_objects.feature;

import java.util.List;
import java.util.Map;

@io.candydoc.ddd.annotations.Aggregate
public class Aggregate {
  private Map<ValueObject, Object> map;
  private List<ValueObjectTwo> stringList;
}
