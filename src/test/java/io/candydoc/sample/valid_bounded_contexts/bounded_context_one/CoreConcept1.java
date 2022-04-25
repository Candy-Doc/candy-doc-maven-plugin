<<<<<<<< HEAD:src/test/java/io/candydoc/sample/valid_bounded_contexts/bounded_context_one/sub_package/CoreConcept1.java
package io.candydoc.sample.valid_bounded_contexts.bounded_context_one.sub_package;
========
package io.candydoc.sample.valid_bounded_contexts.bounded_context_one;
>>>>>>>> 715df6d (Architecture refactoring):src/test/java/io/candydoc/sample/valid_bounded_contexts/bounded_context_one/CoreConcept1.java

import io.candydoc.ddd.annotations.CoreConcept;

@CoreConcept(
    name = "name of core concept 1 of bounded context 1",
    description = "description of core concept 1 of bounded context 1")
public class CoreConcept1 {
  CoreConcept2 concept2;
  ValueObject1 valueObject1;
}
