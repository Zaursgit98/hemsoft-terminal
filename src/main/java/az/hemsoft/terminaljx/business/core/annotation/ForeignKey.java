package az.hemsoft.terminaljx.business.core.annotation;

import az.hemsoft.terminaljx.business.core.enums.ConstraintMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static az.hemsoft.terminaljx.business.core.enums.ConstraintMode.CONSTRAINT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({})
@Retention(RUNTIME)
public @interface ForeignKey {


    String name() default "";


    ConstraintMode value() default CONSTRAINT;

    /**
     * (Optional) The foreign key constraint definition.
     */
    String foreignKeyDefinition() default "";
}





