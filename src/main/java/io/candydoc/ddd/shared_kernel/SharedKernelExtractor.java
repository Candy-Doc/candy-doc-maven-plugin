package io.candydoc.ddd.shared_kernel;

import io.candydoc.ddd.Event;
import io.candydoc.ddd.extract_ddd_concepts.DDDConceptFinder;
import io.candydoc.ddd.extract_ddd_concepts.ExtractDDDConcepts;
import io.candydoc.ddd.model.Extractor;
import io.candydoc.ddd.model.PackageName;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SharedKernelExtractor implements Extractor<ExtractDDDConcepts> {
  private final DDDConceptFinder DDDConceptFinder;

  @Override
  public List<Event> extract(ExtractDDDConcepts command) {
    return command.getPackagesToScan().stream()
        .map(this::extractSharedKernel)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<Event> extract(ExtractSharedKernels command) {
    return extractSharedKernel(command.getPackageToScan()).stream()
        .collect(Collectors.toUnmodifiableList());
  }

  public List<Event> extractSharedKernel(String packageToScan) {
    Set<SharedKernel> sharedKernels =
        DDDConceptFinder.findSharedKernels(PackageName.of(packageToScan));
    log.info("Shared kernels found in {}: {}", packageToScan, sharedKernels);
    return sharedKernels.stream()
        .map(this::toSharedKernelFound)
        .collect(Collectors.toUnmodifiableList());
  }

  private SharedKernelFound toSharedKernelFound(SharedKernel sharedKernel) {
    return SharedKernelFound.builder()
        .simpleName(sharedKernel.getSimpleName().value())
        .canonicalName(sharedKernel.getCanonicalName().value())
        .packageName(sharedKernel.getPackageName().value())
        .description(sharedKernel.getDescription().value())
        .build();
  }
}
