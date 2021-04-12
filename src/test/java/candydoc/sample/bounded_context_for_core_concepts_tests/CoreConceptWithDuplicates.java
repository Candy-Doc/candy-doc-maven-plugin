package candydoc.sample.bounded_context_for_core_concepts_tests;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "name of core concept with duplicates", description = "description of core concept with duplicates")
public class CoreConceptWithDuplicates {

    CoreConcept1 concept1;

    CoreConcept1 returnConcept() {
        CoreConcept1 concept = new CoreConcept1();
        return concept;
    }

    void test(CoreConcept1 concept1) {
    }
}
