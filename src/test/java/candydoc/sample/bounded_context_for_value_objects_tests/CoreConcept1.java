package candydoc.sample.bounded_context_for_value_objects_tests;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "core concept for valueobject interaction", description = "core concept for valueobject")
public class CoreConcept1 {

    ValueObject1 concept1;

    ValueObject2 function() {
        return null;
    }
}
