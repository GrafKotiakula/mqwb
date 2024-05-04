package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

import java.util.Optional;

public class BadRequestException extends RequestException {
    public BadRequestException(Integer code, String clientMessage, String message, Throwable cause) {
        super(code, clientMessage, message, cause);
    }

    public BadRequestException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.badRequestBaseCode;
    }

    public static BadRequestException cannotProcessJson() {
        return cannotProcessJson(null);
    }

    public static BadRequestException cannotProcessJson(Throwable cause) {
        return new BadRequestException(0, "Cannot process JSON", cause);
    }

    public static BadRequestException propertyNotPresented(String fieldName) {
        return propertyNotPresented(fieldName, null);
    }

    public static BadRequestException propertyNotPresented(String fieldName, Throwable cause) {
        return new BadRequestException(1, String.format("%s parameter is required", fieldName),
                String.format("%s property not found", fieldName), cause);
    }

    public static BadRequestException fieldWrongType(String fieldName, String type) {
        return fieldWrongType(fieldName, type, null);
    }

    public static BadRequestException fieldWrongType(String fieldName, String type, Throwable cause) {
        return new BadRequestException(2, String.format("%s parameter must be of %s type", fieldName, type),
                String.format("%s is not %s", fieldName, type), cause);
    }

    public static BadRequestException fieldWrongFormat(String fieldName, String format) {
        return fieldWrongFormat(fieldName, format, null);
    }

    public static BadRequestException fieldWrongFormat(String fieldName, String format, Throwable cause) {
        return new BadRequestException(3, String.format("%s parameter is not of %s format", fieldName, format),
                String.format("wrong %s format", fieldName), cause);
    }

    public static BadRequestException wrongEnumConstant(String fieldName, String constant,
                                                        Class<? extends Enum<?>> enumClazz) {
        return new BadRequestException(4,
                String.format("%s value is unknown enum constant", fieldName),
                String.format("Unknown enum constant %s.%s", enumClazz.getSimpleName(), constant),
                null);
    }

    public static BadRequestException unknownError() {
        return unknownError("Unknown error", null);
    }

    public static BadRequestException unknownError(Throwable cause) {
        return unknownError("Unknown error", cause);
    }

    public static BadRequestException unknownError(String message) {
        return unknownError(message, null);
    }

    public static BadRequestException unknownError(String message, Throwable cause) {
        StringBuilder msgBuilder = new StringBuilder("Unknown error");
        Optional.ofNullable(message).ifPresent(m -> msgBuilder.append(": ").append(m));
        return new BadRequestException(99, message, msgBuilder.toString(), cause);
    }
}
