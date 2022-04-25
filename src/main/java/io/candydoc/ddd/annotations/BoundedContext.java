package io.candydoc.ddd.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedContext {
  String name() default "";

  String description() default "";
}
