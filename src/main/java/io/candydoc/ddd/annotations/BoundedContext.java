package io.candydoc.ddd.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedContext {
  String name() default "";

  String description() default "";
}
