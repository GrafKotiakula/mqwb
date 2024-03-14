package knu.csc.ttp.qualificationwork.mqwb.abstractions.validation;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.util.function.BiFunction;

public class ValidationExceptionGenerator<A extends Annotation, E extends AbstractEntity> {
    private final Class<A> annotation;
    private final BiFunction<ConstraintViolation<E>, String, RequestException> generator;

    public ValidationExceptionGenerator(Class<A> annotation,
                                        BiFunction<ConstraintViolation<E>, String, RequestException> generator) {
        this.annotation = annotation;
        this.generator = generator;
    }

    public RequestException generate(ConstraintViolation<E> violation, String name) {
        return generator.apply(violation, name);
    }

    public BiFunction<ConstraintViolation<E>, String, RequestException> getGenerator() {
        return generator;
    }

    public Class<A> getAnnotation() {
        return annotation;
    }
}
