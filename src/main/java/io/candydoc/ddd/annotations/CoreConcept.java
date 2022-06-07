package io.candydoc.ddd.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface CoreConcept {
  String name() default "";

  String description() default "";
}
