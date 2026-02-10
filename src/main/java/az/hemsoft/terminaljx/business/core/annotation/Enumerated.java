package az.hemsoft.terminaljx.business.core.annotation;

import az.hemsoft.terminaljx.business.core.enums.EnumType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static az.hemsoft.terminaljx.business.core.enums.EnumType.ORDINAL;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;



    @Target({METHOD, FIELD})
    @Retention(RUNTIME)
    public @interface Enumerated {

        EnumType value() default ORDINAL;
    }





