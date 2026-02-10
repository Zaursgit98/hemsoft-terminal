package az.hemsoft.terminaljx.business.core.annotation;


import az.hemsoft.terminaljx.business.core.enums.ConstraintMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface JoinTable {


    String name() default "";


    String catalog() default "";

    String schema() default "";


    JoinColumn[] joinColumns() default {};


    JoinColumn[] inverseJoinColumns() default {};


    ForeignKey foreignKey() default @ForeignKey(ConstraintMode.PROVIDER_DEFAULT);


    ForeignKey inverseForeignKey() default @ForeignKey(ConstraintMode.PROVIDER_DEFAULT);


    UniqueConstraint[] uniqueConstraints() default {};


    Index[] indexes() default {};
}
