package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

public class UnprocessableEntityException extends RequestException {
    public UnprocessableEntityException(Integer code, String clientMessage, String message, Throwable cause) {
        super(code, clientMessage, message, cause);
    }

    public UnprocessableEntityException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.unprocessableEntityBaseCode;
    }

    public static UnprocessableEntityException nullValue(Class<? extends AbstractEntity> clazz, String parameter) {
        return nullValue(clazz, parameter, null);
    }

    public static UnprocessableEntityException nullValue(Class<? extends AbstractEntity> clazz, String parameter,
                                                         Throwable cause) {
        return new UnprocessableEntityException(0, String.format("%s must not be null", parameter),
                String.format("%s.%s is null", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException nullValue(String parameter) {
        return new UnprocessableEntityException(0, String.format("%s must not be null", parameter),
                String.format("%s is null", parameter), null);
    }

    public static UnprocessableEntityException stringTooShort(Class<? extends AbstractEntity> clazz, String parameter,
                                                              int minLength) {
        return stringTooShort(clazz, parameter, minLength, null);
    }

    public static UnprocessableEntityException stringTooShort(Class<? extends AbstractEntity> clazz, String parameter,
                                                              int minLength, Throwable cause) {
        return new UnprocessableEntityException(1, String.format("%s must not be shorter then %d", parameter, minLength),
                String.format("%s.%s is too short", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException stringTooLong(Class<? extends AbstractEntity> clazz, String parameter,
                                                             int maxLength) {
        return stringTooLong(clazz, parameter, maxLength, null);
    }

    public static UnprocessableEntityException stringTooLong(Class<? extends AbstractEntity> clazz, String parameter,
                                                             int maxLength, Throwable cause) {
        return new UnprocessableEntityException(2, String.format("%s must not be longer then %d", parameter, maxLength),
                String.format("%s.%s is too long", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException scaleTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                           int maxScale) {
        return scaleTooBig(clazz, parameter, maxScale, null);
    }

    public static UnprocessableEntityException scaleTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                           int maxScale, Throwable cause) {
        return new UnprocessableEntityException(3,
                String.format("%s scale must not be bigger then %d", parameter, maxScale),
                String.format("%s.%s scale is too big", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException precisionTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                               int maxPrecision) {
        return precisionTooBig(clazz, parameter, maxPrecision, null);
    }

    public static UnprocessableEntityException precisionTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                               int maxPrecision, Throwable cause) {
        return new UnprocessableEntityException(4,
                String.format("%s precision must not be bigger then %d", parameter, maxPrecision),
                String.format("%s.%s precision is too big", clazz.getSimpleName(), parameter),
                cause);
    }

    public static UnprocessableEntityException valueTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                           Long max) {
        return valueTooBig(clazz, parameter, max, null);
    }

    public static UnprocessableEntityException valueTooBig(Class<? extends AbstractEntity> clazz, String parameter,
                                                           Long max, Throwable cause) {
        return new UnprocessableEntityException(5, String.format("%s must not be bigger then %d", parameter, max),
                String.format("%s.%s is too big", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException valueTooSmall(Class<? extends AbstractEntity> clazz, String parameter,
                                                             Long min) {
        return valueTooSmall(clazz, parameter, min, null);
    }

    public static UnprocessableEntityException valueTooSmall(Class<? extends AbstractEntity> clazz, String parameter,
                                                             Long min, Throwable cause) {
        return new UnprocessableEntityException(6, String.format("%s must not be smaller then %d", parameter, min),
                String.format("%s.%s is too small", clazz.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException duplicatedField(Class<? extends AbstractEntity> entity,
                                                               String parameter){
        return duplicatedField(entity, parameter, null);
    }

    public static UnprocessableEntityException duplicatedField(Class<? extends AbstractEntity> entity,
                                                               String parameter, Throwable cause){
        return new UnprocessableEntityException(7, String.format("%s is already used", parameter),
                String.format("%s.%s is duplicated", entity.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException stringIsBlank(Class<? extends AbstractEntity> entity, String parameter){
        return stringIsBlank(entity, parameter, null);
    }

    public static UnprocessableEntityException stringIsBlank(Class<? extends AbstractEntity> entity, String parameter,
                                                             Throwable cause){
        return new UnprocessableEntityException(8, String.format("%s must not be blank", parameter),
                String.format("%s.%s is blank", entity.getSimpleName(), parameter), cause);
    }

    public static UnprocessableEntityException wrongUsernameFormat(Class<? extends AbstractEntity> clazz,
                                                                   String parameter){
        return new UnprocessableEntityException(9,
                String.format("%s may contain digits, latin letters, dots, dashes and underscores", parameter),
                String.format("%s.%s is in the wrong format", clazz.getSimpleName(), parameter), null);
    }

    public static UnprocessableEntityException wrongPasswordFormat(Class<? extends AbstractEntity> clazz,
                                                                   String parameter) {
        return new UnprocessableEntityException(10,
                String.format("%s must contain digits, uppercased and lowercased latin letters", parameter),
                String.format("%s.%s is in the wrong format", clazz.getSimpleName(), parameter), null);
    }

    public static UnprocessableEntityException notPrettyLine(Class<? extends AbstractEntity> clazz,
                                                             String parameter) {
        return new UnprocessableEntityException(11,
                String.format("%s must be a single line that neither starts nor ends with whitespaces", parameter),
                String.format("%s.%s is in the wrong format", clazz.getSimpleName(), parameter), null);
    }

    public static UnprocessableEntityException fileIsNotAnImage(String parameter, Throwable cause) {
        return new UnprocessableEntityException(12,
                String.format("%s must be a valid image", parameter),
                String.format("%s is not a valid image", parameter),
                cause);
    }
}
