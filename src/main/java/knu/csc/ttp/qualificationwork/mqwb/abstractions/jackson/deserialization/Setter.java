package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

public class Setter {
    protected static final Logger logger = LogManager.getLogger(Setter.class);
    private String propertyName;
    private boolean required;
    private Method method;
    private Class<?> parameterType;

    public Setter(){}

    public Setter(Method method) {
        this.parameterType = getParameterType(method);
        this.required = isRequired(method);
        this.propertyName = getPropertyName(method);
        this.method = method;
    }

    private String getPropertyName(Method method) {
        return Optional.ofNullable(method.getAnnotation(JsonProperty.class))
                .map(JsonProperty::value)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> buildPropertyNameFromMethodName(method));
    }

    private String buildPropertyNameFromMethodName(Method method) {
        String methodName = ReflectionUtils.getPropertyNameFromMethodName(method);

        // add id suffix for entities
        if( ReflectionUtils.isEntity(getParameterType(method)) ) {
            methodName += "Id";
        }

        return methodName;
    }

    private boolean isRequired(Method method) {
        return Optional.ofNullable(method.getAnnotation(JsonProperty.class))
                .map(JsonProperty::required)
                .orElse(true);
    }

    private Class<?> getParameterType(Method method) {
        return method.getParameters()[0].getType();
    }

    public void invoke(AbstractEntity entity, Object value) {
        try {
            method.invoke(entity, value);
        } catch (Exception ex) {
            throw LoggerUtils.errorException(logger, InternalServerErrorException.cannotAccessExecutable(method, ex));
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
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

