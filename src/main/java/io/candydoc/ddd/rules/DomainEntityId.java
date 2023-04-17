package io.candydoc.ddd.rules;

import io.candydoc.ddd.model.CanonicalName;
import java.lang.reflect.Type;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class DomainEntityId {
  private final Type type;
  private final CanonicalName name;
}
