package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEntitySerializer <E extends AbstractEntity> extends StdSerializer<E> {
    private static final Map<Class<? extends AbstractEntity>, List<Getter>> entityGettersMap = new HashMap<>();
    public static final SerializeType defaultEntitySerializeType;
    static {
        SerializeType tmp;
        try{
            tmp = (SerializeType) JsonEntity.class
                    .getDeclaredMethod("value")
                    .getDefaultValue();
        } catch (NoSuchMethodException | TypeNotPresentException ex) {
            tmp = SerializeType.FULL;
        }
        defaultEntitySerializeType = tmp;
    }

    protected final Logger logger = LogManager.getLogger(getClass());

    public AbstractEntitySerializer(Class<E> clazz) {
        super(clazz);
    }

    protected List<Getter> getGettersForEntity(Class<? extends AbstractEntity> clazz) {
        if(!entityGettersMap.containsKey(clazz)) {
            List<Getter> getterStream = Arrays.stream(clazz.getMethods())
                    .filter( m -> m.getParameterCount() == 0
                            && (m.isAnnotationPresent(JsonProperty.class) || m.isAnnotationPresent(JsonEntity.class)) )
                    .map(Getter::new)
                    .toList();
            entityGettersMap.put(clazz, getterStream);
        }
        return entityGettersMap.get(clazz);
    }

    private void serializeValue(Getter getter, AbstractEntity entity, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        Object value = getter.invoke(entity);
        SerializeType serializeType = getter.getEntitySerializeType();

        if(value != null && serializeType != null) {
            serializeEntity((AbstractEntity) value, gen, provider, serializeType);
        } else if (value instanceof ZonedDateTime) {
            gen.writeString( ((ZonedDateTime) value).format(Constants.dateTimeFormatter) );
        } else {
            provider.defaultSerializeValue(value, gen);
        }
    }

    private void serializeEntity(AbstractEntity entity, JsonGenerator gen, SerializerProvider provider,
                                 SerializeType type) throws IOException {
        switch(type) {
            case ID_ONLY -> provider.defaultSerializeValue(entity.getId(), gen);
            case FULL -> {
                gen.writeStartObject();
                for( Getter getter: getGettersForEntity(entity.getClass()) ) {
                    gen.writeFieldName(getter.getName());
                    serializeValue(getter, entity, gen, provider);
                }
                gen.writeEndObject();
            }
            default -> throw LoggerUtils.errorException(logger, InternalServerErrorException
                    .unknownEntitySerializingType(type));
        }
    }

    @Override
    public void serialize(E entity, JsonGenerator gen, SerializerProvider provider) throws IOException {
        serializeEntity(entity, gen, provider, SerializeType.FULL);
    }

}
