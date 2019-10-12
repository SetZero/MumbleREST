package utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    private static Log ourInstance = new Log();
    private final Logger privateLogger = LogManager.getFormatterLogger("Main Log");

    public static Log getInstance() {
        return ourInstance;
    }

    public static Logger get() {
        return ourInstance.getLogger();
    }

    private Log() {
    }

    public Logger getLogger() {
        return privateLogger;
    }
}