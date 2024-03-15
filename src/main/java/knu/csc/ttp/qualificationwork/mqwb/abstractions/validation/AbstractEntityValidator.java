package knu.csc.ttp.qualificationwork.mqwb.abstractions.validation;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.UnprocessableEntityException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class AbstractEntityValidator<E extends AbstractEntity> {
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";

    protected final Logger logger = LogManager.getLogger(getClass());
    protected final Class<E> clazz;
    protected final List<Field> fields;
    protected final List<BiConsumer<Object, Field>> validators;

    protected Level defaultLogLvl = Constants.defaultValidationLogLvl;

    public AbstractEntityValidator(Class<E> clazz) {
        this.clazz = clazz;
        this.fields = Arrays.stream(clazz.getDeclaredFields()).peek(f -> f.setAccessible(true)).toList();
        validators = Arrays.asList(
                this::validateJpaColumn,
                this::validateJpaJoinColumn,
                this::validateSize,
                this::validateMin,
                this::validateMax,
                this::validateNotBlank,
                this::validatePattern
        );
    }

    protected int getMaxLength(Field field) {
        return Math.min(
                Optional.ofNullable(field.getAnnotation(Column.class)).map(Column::length).orElse(Integer.MAX_VALUE),
                Optional.ofNullable(field.getAnnotation(Size.class)).map(Size::max).orElse(Integer.MAX_VALUE)
        );
    }

    protected void validateNotBlank(Object obj, Field field) {
        NotBlank notBlank = field.getAnnotation(NotBlank.class);
        Object value = getFieldValue(obj, field);
        if(notBlank != null && value instanceof String && ((String)value).isBlank()) {
            throw LoggerUtils.logException( logger, defaultLogLvl, UnprocessableEntityException
                    .stringIsBlank(clazz, field.getName()) );
        }
    }

    protected void validatePattern(Object obj, Field field) {
        Pattern pattern = field.getAnnotation(Pattern.class);
        Object value = getFieldValue(obj, field);
        if(pattern != null && value instanceof String str) { // value not null
            if( !str.matches(pattern.regexp()) ) {
                RequestException exception;
                try{
                    exception = (UnprocessableEntityException) UnprocessableEntityException.class
                            .getMethod(pattern.message(), Class.class, String.class)
                            .invoke(null, clazz, field.getName());
                } catch (Exception ex) {
                    logger.warn("Cannot access method {}.{}[Class, String]: {}",
                            UnprocessableEntityException.class.getSimpleName(), pattern.message(), ex.getMessage());
                    exception = BadRequestException
                            .unknownError(String.format("%s.%s invalid format", clazz.getSimpleName(), field.getName()));
                }
                throw LoggerUtils.logException(logger, defaultLogLvl, exception);
            }
        }
    }

    protected void validateMax(Object obj, Field field) {
        Max max = field.getAnnotation(Max.class);
        Object value = getFieldValue(obj, field);
        if(max != null && value != null) {
            if( (value instanceof Long && max.value() < (Long) value)
                    || (value instanceof Integer && max.value() < (Integer) value)
                    || (value instanceof Double && max.value() < (Double) value)
                    || (value instanceof Float && max.value() < (Float) value) ){
                throw LoggerUtils.logException(logger, defaultLogLvl,
                        UnprocessableEntityException.valueTooBig(clazz, field.getName(), max.value()) );
            }
        }
    }

    protected void validateMin(Object obj, Field field) {
        Min min = field.getAnnotation(Min.class);
        Object value = getFieldValue(obj, field);
        if(min != null && value != null) {
            if( (value instanceof Long && min.value() > (Long) value)
                    || (value instanceof Integer && min.value() > (Integer) value)
                    || (value instanceof Double && min.value() > (Double) value)
                    || (value instanceof Float && min.value() > (Float) value) ){
                throw LoggerUtils.logException(logger, defaultLogLvl,
                        UnprocessableEntityException.valueTooSmall(clazz, field.getName(), min.value()) );
            }
        }
    }

    protected void validateSize(Object obj, Field field) {
        Size size = field.getAnnotation(Size.class);
        Object value = getFieldValue(obj, field);

        if(size != null && value != null) {
            if(value instanceof String str) {
                if(str.length() < size.min()) {
                    throw LoggerUtils.logException(logger, defaultLogLvl,
                            UnprocessableEntityException.stringTooShort(clazz, field.getName(), size.min()) );
                } else if (str.length() > size.max()) {
                    throw LoggerUtils.logException(logger, defaultLogLvl,
                            UnprocessableEntityException.stringTooLong(clazz, field.getName(), getMaxLength(field)) );
                }
            }
        }
    }

    private void validateJpaColumn(Column column, BigDecimal value, Field field) {
        if(column.scale() < value.scale()) {
            throw LoggerUtils.logException(logger, defaultLogLvl,
                    UnprocessableEntityException.scaleTooBig(clazz, field.getName(), column.scale()) );
        } else if (column.precision() < value.precision()) {
            throw LoggerUtils.logException(logger, defaultLogLvl,
                    UnprocessableEntityException.precisionTooBig(clazz, field.getName(), column.precision()) );
        }
    }

    private void validateJpaColumn(Column column, String value, Field field) {
        if(column.length() < value.length()) {
            throw LoggerUtils.logException(logger, defaultLogLvl,
                    UnprocessableEntityException.stringTooLong(clazz, field.getName(), getMaxLength(field)) );
        }
    }

    protected void validateJpaColumn(Object obj, Field field) {
        Column column = field.getAnnotation(Column.class);

        if(column != null) {
            Object value = getFieldValue(obj, field);

            if(!column.nullable() && value == null) {
                throw LoggerUtils.logException( logger, defaultLogLvl,
                        UnprocessableEntityException.nullValue(clazz, field.getName()) );
            }
            if(value instanceof String) {
                validateJpaColumn(column, (String) value, field);
            }
            if(value instanceof BigDecimal) {
                validateJpaColumn(column, (BigDecimal) value, field);
            }
        }
    }

    protected void validateJpaJoinColumn(Object obj, Field field) {
        Optional.ofNullable(field.getAnnotation(JoinColumn.class))
                .filter(jc -> !jc.nullable() && getFieldValue(obj, field) == null)
                .ifPresent(jc -> LoggerUtils.logException( logger, defaultLogLvl,
                        UnprocessableEntityException.nullValue(clazz, field.getName()) ));
    }

    protected Object getFieldValue(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch(Exception ex) {
            throw LoggerUtils.errorException(logger, InternalServerErrorException.cannotAccessField(field, ex));
        }
    }

    protected void validateFiled(E entity, Field field) {
        validators.forEach(v -> v.accept(entity, field));
    }

    public void validate(E entity) {
        validate(entity, null);
    }

    public void validate(E entity, String validationGroup) {
        Optional.ofNullable(validationGroup)
                .map(group -> fields.stream()
                        .filter(f -> Optional.ofNullable(f.getAnnotation(ValidationGroup.class))
                                .map(vg -> (vg.value().length == 0 && !Arrays.asList(vg.exclude()).contains(group))
                                        || Arrays.asList(vg.value()).contains(group))
                                .orElse(true)))
                .orElse(fields.stream())
                .forEach(f -> validateFiled(entity, f));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void validateUniqueness(E entity, Optional<E> foundCopy, Level logLvl, String filedName) {
        foundCopy.filter( e -> !e.getId().equals(entity.getId()) ).ifPresent(e -> {
            throw LoggerUtils.logException(logger, logLvl,
                    UnprocessableEntityException.duplicatedField(clazz, filedName));
        });
    }

    public Level getDefaultLogLvl() {
        return defaultLogLvl;
    }

    public void setDefaultLogLvl(Level defaultLogLvl) {
        this.defaultLogLvl = defaultLogLvl;
    }
}
