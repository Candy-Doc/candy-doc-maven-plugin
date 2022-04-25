package io.candydoc.ddd.value_object;

import io.candydoc.ddd.Event;
import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.ddd.annotations.DomainEvent(
    description = "Emitted when a value object is found in a bounded context")
public class ValueObjectFound implements Event {
  @NonNull String description;
  @NonNull String name;
  @NonNull String className;
  @NonNull String packageName;
  @NonNull String boundedContext;

  public void accept(Visitor visitor) {
    visitor.apply(this);
  }
}
