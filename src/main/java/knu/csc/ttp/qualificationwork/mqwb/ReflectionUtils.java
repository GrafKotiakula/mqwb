package knu.csc.ttp.qualificationwork.mqwb;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionUtils {
    private static final Logger logger = LogManager.getLogger(ReflectionUtils.class);

    @SuppressWarnings("unchecked")
    public static <B, T extends B, G> Class<? extends B> getTypeClassOfGenericClass(G curImpl, Class<G> genericClass,
                                                                                    Class<B> defaultVal,
                                                                                    int typeIndex) {
        if(curImpl.getClass().getSuperclass().equals(genericClass)) {
            ParameterizedType ptype = (ParameterizedType) curImpl.getClass().getGenericSuperclass();
            return (Class<T>) ptype.getActualTypeArguments()[typeIndex];
        } else {
            return defaultVal;
        }
    }

    public static <B, T extends B, G> Class<? extends B> getFirstTypeClassOfGenericClass(G curImpl,
                                                                                         Class<G> genericClass,
                                                                                         Class<B> defaultVal) {
        return getTypeClassOfGenericClass(curImpl, genericClass, defaultVal, 0);
    }

    public static String getPropertyNameFromMethodName(Method method) {
        String methodName = method.getName();

        // remove standard prefixes
        for(String prefix: Arrays.asList("set", "get", "is")) {
            int prefixLength = prefix.length();
            if (methodName.startsWith(prefix) && Character.isUpperCase(methodName.charAt(prefixLength))) {
                methodName = Character.toLowerCase(methodName.charAt(prefixLength))
                        + methodName.substring(prefixLength + 1);
                break;
            }
        }

        return methodName;
    }

    public static boolean isEntity(Class<?> clazz) {
        return AbstractEntity.class.isAssignableFrom(clazz);
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Level exceptionLogLevel, Class<?>... types)
            throws InternalServerErrorException{
        try {
            return clazz.getConstructor(types);
        } catch (NoSuchMethodException | SecurityException ex) {
            InternalServerErrorException exception = InternalServerErrorException.cannotGetAccessibleObject(ex);
            Optional.ofNullable(exceptionLogLevel)
                    .ifPresent(l -> LoggerUtils.logException(logger, exceptionLogLevel, exception));
            throw exception;
        }
    }

    public static <T> T buildObject(Constructor<T> constr, Level exceptionLogLevel, Object... args)
            throws InternalServerErrorException {
        try {
            return constr.newInstance(args);
        } catch (Exception ex) {
            InternalServerErrorException exception = InternalServerErrorException.cannotAccessExecutable(constr, ex);
            Optional.ofNullable(exceptionLogLevel)
                    .ifPresent(l -> LoggerUtils.logException(logger, exceptionLogLevel, exception));
            throw exception;
        }
    }

    public static <T> T buildObject(Class<T> clazz, Level exceptionLogLevel, Object... args)
            throws InternalServerErrorException{
        Constructor<T> constructor = getConstructor(clazz, exceptionLogLevel,
                Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
        return buildObject(constructor, exceptionLogLevel, args);
    }
}
