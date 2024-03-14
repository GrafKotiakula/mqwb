package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class Setter {
    protected static final Logger logger = LogManager.getLogger(Setter.class);
    private String name;
    private boolean required;
    private Method method;
    private Class<?> parameterType;

    public Setter(){}

    public Setter(Method method) {
        this.name = ReflectionUtils.getJsonPropertyName(method);
        this.required = method.getAnnotation(JsonProperty.class).required();
        this.parameterType = method.getParameters()[0].getType();
        this.method = method;
    }

    public void invoke(AbstractEntity entity, Object value) {
        try {
            method.invoke(entity, value);
        } catch (Exception ex) {
            throw LoggerUtils.errorException(logger, InternalServerErrorException.cannotAccessExecutable(method, ex));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }
}

