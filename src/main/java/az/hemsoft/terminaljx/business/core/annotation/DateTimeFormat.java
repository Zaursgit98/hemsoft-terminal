package az.hemsoft.terminaljx.business.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateTimeFormat {
    String style() default "SS";

    ISO iso() default DateTimeFormat.ISO.NONE;

    String pattern() default "";

    String[] fallbackPatterns() default {};

    public static enum ISO {
        DATE,
        TIME,
        DATE_TIME,
        NONE;
    }
}
