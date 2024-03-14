package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonEntity {
    SerializeType value() default SerializeType.FULL;
}
