package io.candydoc.ddd.tactical_pattern;

import io.candydoc.ddd.model.DDDPattern;
import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum TacticalPattern implements DDDPattern {
  AGGREGATE(io.candydoc.ddd.annotations.Aggregate.class) {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.aggregate();
    }
  },
  DOMAIN_COMMAND(io.candydoc.ddd.annotations.DomainCommand.class) {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.domainCommand();
    }
  },
  DOMAIN_EVENT(io.candydoc.ddd.annotations.DomainEvent.class) {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.domainEvent();
    }
  },
  VALUE_OBJECT(io.candydoc.ddd.annotations.ValueObject.class) {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.valueObject();
    }
  };

  private final Class<? extends Annotation> relatedAnnotation;
}
