package io.candydoc.sample.valid_bounded_contexts.bounded_context_one;

import io.candydoc.ddd.annotations.CoreConcept;

@CoreConcept(
    name = "name of core concept 1 of bounded context 1",
    description = "description of core concept 1 of bounded context 1")
public class CoreConcept1 {
  CoreConcept2 concept2;
  ValueObject1 valueObject1;
}
