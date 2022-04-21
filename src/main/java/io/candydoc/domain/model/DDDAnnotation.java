package io.candydoc.domain.model;

import java.lang.annotation.Annotation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DDDAnnotation {
  private Class<? extends Annotation> annotation;
}
