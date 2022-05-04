package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedKernelInteractionStrategy implements InteractionStrategy<SharedKernel> {

  @NonNull private final DDDConceptFinder conceptFinder;

  public List<Event> checkInteractions(SharedKernel concept) {
    return List.of();
  }
}
