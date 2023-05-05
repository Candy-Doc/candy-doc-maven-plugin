package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.model.Dependency;
import io.candydoc.ddd.strategic_pattern.StrategicModel;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedStrategicPatternsForbidden extends ExtractionException {

  public NestedStrategicPatternsForbidden(StrategicModel strategicModel) {
    super(toMessage(strategicModel));
  }

  private static String toMessage(StrategicModel strategicModel) {
    List<String> nestedStrategicModels =
        strategicModel.forbiddenDependencies().stream()
            .map(Dependency::name)
            .map(name -> String.format("- %s", name.value()))
            .collect(Collectors.toUnmodifiableList());

    return Stream.of(
            List.of(
                String.format(
                    "'%s' contains nested strategic patterns:",
                    strategicModel.simpleName().value())),
            nestedStrategicModels)
        .flatMap(Collection::stream)
        .collect(Collectors.joining("\n"));
  }
}
