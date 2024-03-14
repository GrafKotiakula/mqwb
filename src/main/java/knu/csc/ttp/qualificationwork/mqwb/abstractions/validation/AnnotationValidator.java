package knu.csc.ttp.qualificationwork.mqwb.abstractions.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class AnnotationValidator<A extends Annotation> {
    private final Class<A> clazz;
    private final BiConsumer<Object, Field> validator;

    public AnnotationValidator(Class<A> clazz, BiConsumer<Object, Field> validator) {
        this.clazz = clazz;
        this.validator = validator;
    }

    public void validate(Object object, Field field) {
        validator.accept(object, field);
    }

    public Class<A> getClazz() {
        return clazz;
    }
}
