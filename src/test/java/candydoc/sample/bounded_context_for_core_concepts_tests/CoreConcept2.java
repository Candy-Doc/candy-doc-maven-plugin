package candydoc.sample.bounded_context_for_core_concepts_tests;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "name of core concept 2", description = "description of core concept 2")
public class CoreConcept2 {

    CoreConcept1 returnConcept() {
        CoreConcept1 concept = new CoreConcept1();
        return concept;
    }
}
