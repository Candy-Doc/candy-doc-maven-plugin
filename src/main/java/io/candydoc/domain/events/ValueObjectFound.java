package io.candydoc.domain.events;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
@io.candydoc.domain.annotations.DomainEvent(
    description = "Emitted when a value object is found in a bounded context")
public class ValueObjectFound implements DomainEvent {
  @NonNull String description;
  @NonNull String name;
  @NonNull String className;
  @NonNull String packageName;
  @NonNull String boundedContext;

  public void accept(Visitor visitor) {
    visitor.apply(this);
  }
}
