package candydoc.sample.bounded_context_for_core_concepts_tests;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "name of core concept 1", description = "description of core concept 1")
public class CoreConcept1 {

    CoreConcept2 concept2;
}
