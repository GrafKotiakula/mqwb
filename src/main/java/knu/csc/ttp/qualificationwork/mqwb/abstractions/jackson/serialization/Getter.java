package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

public class Getter {
    private final Logger logger = LogManager.getLogger(getClass());
    private Method method;
    private String name;
    private Class<?> type;
    private SerializeType entitySerializeType;

    public Getter() {}

    public Getter(Method method) {
        this.method = method;
        this.name = ReflectionUtils.getJsonPropertyName(method);
        this.type = method.getReturnType();
        if(ReflectionUtils.isEntity(type)){
            this.entitySerializeType = Optional.ofNullable(method.getAnnotation(JsonEntity.class))
                    .map(JsonEntity::value).orElse(AbstractEntitySerializer.defaultEntitySerializeType);
        }
    }

    public Object invoke(AbstractEntity entity) {
        try {
            return method.invoke(entity);
        } catch (Exception ex) {
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
