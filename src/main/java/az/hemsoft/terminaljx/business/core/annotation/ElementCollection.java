package az.hemsoft.terminaljx.business.core.annotation;


import com.sun.jdi.Method;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ElementCollection {
    Class targetClass() default void.class;

    FetchType fetch() default FetchType.LAZY;
}
