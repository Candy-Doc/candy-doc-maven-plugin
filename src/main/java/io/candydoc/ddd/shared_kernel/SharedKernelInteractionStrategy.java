package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.interaction.InteractionStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedKernelInteractionStrategy implements InteractionStrategy<SharedKernel> {

  public List<Event> checkInteractions(SharedKernel concept) {
    return List.of();
  }
}
