package candydoc.sample.bounded_context_for_core_concepts_tests;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "name of core concept 3", description = "description of core concept 3")
public class CoreConcept3 {

    void test(CoreConcept1 concept1) {
    }
}
