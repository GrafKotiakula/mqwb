package knu.csc.ttp.qualificationwork.mqwb;

import org.apache.logging.log4j.Level;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String dateFormat = "yyyy-MM-dd";
    public static final String timeFormat = "HH:mm:ss Z";
    public static final String dateTimeFormat = dateFormat + " " + timeFormat;
    public static final String timeZone = "UTC";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)
            .withZone(ZoneId.of(timeZone));

    public static final Integer authExceptionBaseCode = 1000;
    public static final Integer badRequestBaseCode = 1100;
    public static final Integer forbiddenBaseCode = 1200;
    public static final Integer notFoundBaseCode = 1300;
    public static final Integer unprocessableEntityBaseCode = 1400;
    public static final Integer methodNotAllowedBaseCode = 1500;
    public static final Integer insufficientStorageBaseCode = 1800;
    public static final Integer internalServerErrorBaseCode = 1900;

    public static final String projectRootPackage = "knu.csc.ttp.qualificationwork.mqwb";
    public static final String serviceLoggerName = "SERVICE";
    public static final String controllerLoggerName = "CONTROLLER";
    public static final String serializerLoggerName = "SERIALIZER";
    public static final String deserializerLoggerName = "DESERIALIZER";
    public static final String validatorLoggerName = "VALIDATOR";
    public static final String securityLoggerName = "SECURITY";

    public static final Level defaultCreateLogLvl = Level.INFO;
    public static final Level defaultUpdateLogLvl = Level.INFO;
    public static final Level defaultDeleteLogLvl = Level.INFO;
    public static final Level defaultValidationLogLvl = Level.DEBUG;
    public static final Level defaultJsonDeserializationLogLvl = Level.DEBUG;
    public static final Level defaultControllerLogLvl = Level.DEBUG;
    public static final Level defaultSecurityLogLvl = Level.DEBUG;

    public static final int pageSize = 20;

    public static final String prettyLineRegexp = "^\\S[^\r\n]*\\S"; // used for @Pattern.regexp
    public static final String prettyLineMessage = "notPrettyLine";  // used for @Pattern.message
}
