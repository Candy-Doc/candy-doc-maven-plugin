package io.candydoc.ddd.sample.generic_types.feature;

import java.util.List;
import java.util.Map;
import java.util.Set;

@io.candydoc.ddd.annotations.Aggregate
public class Aggregate {
  private Map<ValueObject, Object> map;
  private List<ValueObjectTwo> stringList;
  private Map<Integer, List<List<Set<ValueObjectThree>>>> listMap;
  private Map<DomainCommand, DomainEvent> mapCmdEvent;
}
