package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.NotFoundException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractEntityDeserializer<E extends AbstractEntity> extends StdDeserializer<E> {
    protected static Map<Class<?>, JsonValueExtractor<?>> extractors;
    protected static JsonValueExtractor<UUID> uuidExtractor;
    private static final Logger staticLogger = LogManager.getLogger(AbstractEntityDeserializer.class);
    static {
        uuidExtractor = new JsonValueExtractor<>(UUID.class, "id", "string[uuid]",
                JsonNode::isTextual, AbstractEntityDeserializer::extractUUID);
        extractors = Stream.of(
                new JsonValueExtractor<>(String.class,"string","string",JsonNode::isTextual,
                        (node, name) -> node.textValue()),
                new JsonValueExtractor<>(Integer.class,"integer","integer",JsonNode::isInt,
                        (node, name) -> node.intValue()),
                new JsonValueExtractor<>(Boolean.class,"bool","bool",JsonNode::isBoolean,
                        (node, name) -> node.booleanValue()),
                new JsonValueExtractor<>(ZonedDateTime.class,"timestamp",String.format("string[%s]",
                        Constants.dateTimeFormat), JsonNode::isTextual, AbstractEntityDeserializer::extractZonedDateTime),
                uuidExtractor
        ).collect( Collectors.toMap(JsonValueExtractor::getClazz, Function.identity()) );
    }

    protected final Logger logger = LogManager.getLogger(getClass());
    private final Constructor<E> constructor;
    private final List<Setter> setters;

    protected Level defaultLogLvl = Constants.defaultJsonDeserializationLogLvl;
    protected Repositories repositories;

    @SuppressWarnings("unused")
    protected AbstractEntityDeserializer(ApplicationContext context, Class<E> clazz) {
        super(clazz);
        this.repositories = new Repositories(context);
        try{
            this.constructor = ReflectionUtils.getConstructor(clazz, Level.FATAL);
        } catch (InternalServerErrorException ex) {
            // TODO
            ((ConfigurableApplicationContext) context).close();
            logger.fatal("Fail to create {}", getClass().getSimpleName());
            throw ex;
        }
        this.setters = extractAnnotatedSetters(clazz);
    }

    private static ZonedDateTime extractZonedDateTime(JsonNode node, String name) {
        try {
            return ZonedDateTime.parse(node.textValue(), Constants.dateTimeFormatter);
        } catch (DateTimeParseException ex) {
            throw LoggerUtils.debugException(staticLogger, BadRequestException
                    .fieldWrongFormat(name,Constants.dateTimeFormat,ex));
        }
    }

    private static UUID extractUUID(JsonNode node, String name) {
        try {
            return UUID.fromString(node.textValue());
        } catch (IllegalArgumentException ex) {
            throw LoggerUtils.debugException(staticLogger, BadRequestException.fieldWrongFormat(name,"uuid",ex));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractEntity> JsonValueExtractor<T> createEntityExtractorById(Class<T> entityClazz) {
        JpaRepository<T, UUID> repo = (JpaRepository<T, UUID>) repositories.getRepositoryFor(entityClazz)
                .orElseThrow(() -> LoggerUtils.errorException(logger,
                        InternalServerErrorException.repositoryNotFound(entityClazz)) );
        return new JsonValueExtractor<>(entityClazz, entityClazz.getSimpleName(),"string[uuid]",
                uuidExtractor.getTypeCheckPredicate(), (node, name) -> Optional
                .ofNullable(uuidExtractor.extractValue(node))
                .map(uuid -> repo.findById(uuid).orElseThrow( () -> LoggerUtils
                        .logException(logger, defaultLogLvl, NotFoundException.idNotFound(entityClazz, uuid)) ))
                .orElse(null) );
    }

    @SuppressWarnings("unchecked")
    protected <T> JsonValueExtractor<T> findBasicExtractorForType(Class<T> type) {
        JsonValueExtractor<T> extractor = (JsonValueExtractor<T>) extractors.get(type);
        if (extractor != null) {
            return extractor;
        } else if (AbstractEntity.class.isAssignableFrom(type)){
            extractor = (JsonValueExtractor<T>) createEntityExtractorById( (Class<? extends AbstractEntity>) type );
            extractors.put(type, extractor);
            return extractor;
        } else {
            throw LoggerUtils.errorException(logger, InternalServerErrorException.noExtractorFound(type));
        }
    }

    private E newEntity() {
        return ReflectionUtils.buildObject(constructor, Level.ERROR);
    }

    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        E entity = newEntity();
        return deserialize(jsonParser, context, entity);
    }

    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext context, E entity) throws IOException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        for(Setter s: setters){
            Object value = findBasicExtractorForType(s.getParameterType()).extractProperty(root, s.getName());
            s.invoke(entity, value);
        }
        return entity;
    }

    private List<Setter> extractAnnotatedSetters(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> m.getParameterCount() == 1 && m.isAnnotationPresent(JsonProperty.class))
                .map(Setter::new)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(JsonNode node, String propertyName, Class<T> propertyClazz) {
        JsonValueExtractor<T> extractor = (JsonValueExtractor<T>) Optional.ofNullable(extractors.get(propertyClazz))
                .orElseThrow( () -> LoggerUtils.errorException(staticLogger,
                        InternalServerErrorException.noExtractorFound(propertyClazz)) );
        return extractor.extractProperty(node, propertyName);
    }

    public Level getDefaultLogLvl() {
        return defaultLogLvl;
    }

    public void setDefaultLogLvl(Level defaultLogLvl) {
        this.defaultLogLvl = defaultLogLvl;
    }
}
