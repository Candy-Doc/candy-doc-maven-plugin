package io.candydoc.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DDDField {
  private String name;
  private Class<?> type;
}
