package io.candydoc.domain.model;

import lombok.*;

@Builder
@ToString
@Value
@RequiredArgsConstructor
public class BoundedContext {
    @NonNull
    String name;
    @NonNull
    String description;
}
