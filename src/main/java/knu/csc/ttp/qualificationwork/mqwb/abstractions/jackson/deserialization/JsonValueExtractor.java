package knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class JsonValueExtractor<T> {
    private final Logger logger = LogManager.getLogger(getClass());
    private final Class<T> clazz;
    private final String jsonTypeName;
    private final String defaultName;
    private final Predicate<JsonNode> typeCheckPredicate;
    private final BiFunction<JsonNode, String, T> valueExtractor;

    public JsonValueExtractor(Class<T> clazz, String defaultName, String jsonTypeName,
                              Predicate<JsonNode> typeCheckPredicate, BiFunction<JsonNode, String, T> valueExtractor) {
        this.clazz = clazz;
        this.jsonTypeName = jsonTypeName;
        this.defaultName = defaultName;
        this.typeCheckPredicate = typeCheckPredicate;
        this.valueExtractor = valueExtractor;
    }

    private T extractValue(JsonNode node, String containerName) {
        String name = Optional.ofNullable(containerName).orElse(defaultName);
        if(node.isNull()) {
            return null;
        } else if(typeCheckPredicate.test(node)) {
            return valueExtractor.apply(node, name);
        } else {
            throw LoggerUtils.debugException(logger, BadRequestException.fieldWrongType(name, jsonTypeName));
        }
    }

    public T extractProperty(JsonNode root, String name) {
        if(!root.isObject()){
            throw LoggerUtils.debugException(logger, BadRequestException.cannotProcessJson());
        }
        if(!root.has(name)){
            throw LoggerUtils.debugException(logger, BadRequestException.propertyNotPresented(name));
        }
        return extractValue(root.get(name), name);
    }

    public T extractValue(JsonNode node) {
        return extractValue(node, null);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Predicate<JsonNode> getTypeCheckPredicate() {
        return typeCheckPredicate;
    }

    public BiFunction<JsonNode, String, T> getValueExtractor() {
        return valueExtractor;
    }

    public String getJsonTypeName() {
        return jsonTypeName;
    }

    public String getDefaultName() {
        return defaultName;
    }
}
