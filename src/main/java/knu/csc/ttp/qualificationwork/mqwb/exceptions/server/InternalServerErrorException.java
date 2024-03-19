package knu.csc.ttp.qualificationwork.mqwb.exceptions.server;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.SerializeType;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InternalServerErrorException extends RequestException {
    protected InternalServerErrorException(String message, Throwable cause) {
        super(0, "Internal server error", message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.internalServerErrorBaseCode;
    }

    public static InternalServerErrorException cannotAccessField(Field field) {
        return cannotAccessField(field, null);
    }

    public static InternalServerErrorException cannotAccessField(Field field, Throwable cause) {
        return cannotAccessField(field.getDeclaringClass(), field.getName(), cause);
    }

    public static InternalServerErrorException cannotAccessField(Class<?> clazz, String fieldName, Throwable cause) {
        return new InternalServerErrorException(String.format("Cannot access field %s.%s",
                clazz.getSimpleName(), fieldName), cause);
    }

    public static InternalServerErrorException cannotAccessExecutable(Executable executable) {
        return cannotAccessExecutable(executable, null);
    }

    public static InternalServerErrorException cannotAccessExecutable(Executable executable, Throwable cause) {
        return new InternalServerErrorException(
                String.format( "Cannot access executable %s.%s(%s)",
                        executable.getDeclaringClass().getSimpleName(),
                        executable.getName(),
                        Arrays.stream(executable.getParameterTypes()).map(Class::getSimpleName)
                                .collect(Collectors.joining(", ")) ),
                cause);
    }

    public static InternalServerErrorException repositoryNotFound(Class<? extends AbstractEntity> entity) {
        return repositoryNotFound(entity, null);
    }

    public static InternalServerErrorException repositoryNotFound(Class<? extends AbstractEntity> entity,
                                                                  Throwable cause) {
        return new InternalServerErrorException( String.format("Cannot find repository for %s", entity.getSimpleName()),
                cause);
    }

    public static InternalServerErrorException noExtractorFound(Class<?> clazz) {
        return noExtractorFound(clazz, null);
    }

    public static InternalServerErrorException noExtractorFound(Class<?> clazz, Throwable cause) {
        return new InternalServerErrorException(
                String.format("No json extractor found for %s", clazz.getSimpleName()), cause);
    }

    public static InternalServerErrorException cannotGetAccessibleObject(Throwable cause) {
        return new InternalServerErrorException("Cannot get accessible object", cause);
    }

    public static InternalServerErrorException unknownEntitySerializingType(SerializeType type) {
        return new InternalServerErrorException(String.format("Unknown entity serializing type %s", type.name()), null);
    }

    public static InternalServerErrorException cannotGetGenericClassTypeParameter(Class<?> clazz, Exception cause) {
        return new InternalServerErrorException
                (String.format("Cannot get %s generic parameter(s)", clazz.getSimpleName()), cause);
    }

    public static InternalServerErrorException tooWeakKey(Throwable cause) {
        return new InternalServerErrorException("Jwt key is too weak", cause);
    }

    public static InternalServerErrorException sqlException(SQLException cause) {
        return new InternalServerErrorException("SQL exception occurs", cause);
    }
}
