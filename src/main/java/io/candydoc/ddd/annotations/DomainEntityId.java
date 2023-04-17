package io.candydoc.ddd.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainEntityId {}
