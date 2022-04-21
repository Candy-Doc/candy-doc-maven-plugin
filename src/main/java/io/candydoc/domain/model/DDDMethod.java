package io.candydoc.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DDDMethod {
  private String name;
  private Class<?> returnType;
  private List<Class<?>> parameterTypes;
}
