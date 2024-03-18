package knu.csc.ttp.qualificationwork.mqwb;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtils {
    public static final Integer INF_DEPTH = -1;

    public static <T extends Throwable> T logException (Logger logger, Level lvl, T exception) {
        return logException(logger, lvl, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T logException (Logger logger, Level lvl, T exception, int maxDepth) {
        StringBuilder resultText = new StringBuilder(exception.getClass().getSimpleName())
                .append(": ")
                .append(exception.getMessage());
        int depth = 1;
        Throwable curException = exception.getCause();
        while (curException != null && (maxDepth < 0 || depth < maxDepth)) {
            depth ++;
            resultText.append(" | Caused by ")
                    .append(curException.getClass().getSimpleName())
                    .append(": ")
                    .append(curException.getMessage().replace("\n", "    "));
            curException = curException.getCause();
        }
        if (curException != null) {
            resultText.append(" | ...");
        }
        logger.log(lvl, resultText.toString());
        return exception;
    }

    public static <T extends Throwable> T fatalException (Logger logger, T exception) {
        return logException(logger, Level.FATAL, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T errorException (Logger logger, T exception) {
        return logException(logger, Level.ERROR, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T warnException (Logger logger, T exception) {
        return logException(logger, Level.WARN, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T infoException (Logger logger, T exception) {
        return logException(logger, Level.INFO, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T debugException (Logger logger, T exception) {
        return logException(logger, Level.DEBUG, exception, INF_DEPTH);
    }

    public static <T extends Throwable> T traceException (Logger logger, T exception) {
        return logException(logger, Level.TRACE, exception, INF_DEPTH);
    }

    public static Logger getNamedLogger(String name, Class<?> clazz) {
        StringBuilder nameBuilder = new StringBuilder(Constants.projectRootPackage)
                .append('.').append(name)
                .append('.').append(clazz.getSimpleName());

        return LogManager.getLogger(nameBuilder.toString());

    }
}
