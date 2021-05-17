package candydoc.sample.bounded_context_for_wrong_usage_of_value_objects;

@io.candydoc.domain.annotations.ValueObject(description = "value object interacting withFullName something it shouldn't")
public class ValueObject {
    void fakeFunction(CoreConcept wrongInteraction) {

    }
}
