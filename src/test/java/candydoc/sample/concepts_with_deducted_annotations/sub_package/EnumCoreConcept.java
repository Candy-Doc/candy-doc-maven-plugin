package candydoc.sample.concepts_with_deducted_annotations.sub_package;

import io.candydoc.domain.annotations.CoreConcept;

@CoreConcept(name = "My enum core concept", description = "My enum core concept description")
public enum EnumCoreConcept {
    A {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitA();
        }
    },
    B {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitB();
        }
    },
    C {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitC();
        }
    };

    public abstract <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visitA();
        T visitB();
        T visitC();
    }
}
