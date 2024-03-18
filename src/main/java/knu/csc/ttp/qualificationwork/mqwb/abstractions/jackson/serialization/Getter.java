package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

public class Getter {
    private final Logger logger = LoggerUtils.getNamedLogger(Constants.serializerLoggerName, getClass());
    private Method method;
    private String name;
    private Class<?> type;
    private SerializeType entitySerializeType;

    public Getter() {}

    public Getter(Method method) {
        this.method = method;
        this.name = getPropertyName(method);
        this.type = method.getReturnType();
        if(ReflectionUtils.isEntity(type)){
            this.entitySerializeType = Optional.ofNullable(method.getAnnotation(JsonEntity.class))
                    .map(JsonEntity::value).orElse(AbstractEntitySerializer.defaultEntitySerializeType);
        }
    }

    private String getPropertyName(Method method) {
        return Optional.ofNullable(method.getAnnotation(JsonProperty.class))
                .map(JsonProperty::value)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> ReflectionUtils.getPropertyNameFromMethodName(method));
    }

    public Object invoke(AbstractEntity entity) {
        try {
            return method.invoke(entity);
        } catch (Exception ex) {
            System.out.println(method.getDeclaringClass().getName());
            System.out.println(entity.getClass().getName());
            throw LoggerUtils.errorException(logger, InternalServerErrorException.cannotAccessExecutable(method, ex));
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public SerializeType getEntitySerializeType() {
        return entitySerializeType;
    }

    public void setEntitySerializeType(SerializeType entitySerializeType) {
        this.entitySerializeType = entitySerializeType;
    }
}
