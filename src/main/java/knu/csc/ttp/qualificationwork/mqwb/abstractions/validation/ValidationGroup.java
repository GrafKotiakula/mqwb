package knu.csc.ttp.qualificationwork.mqwb.abstractions.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationGroup {
    String[] value() default {};
    String[] exclude() default {};
}
