package be.everbuild.autosense;

import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogReceiver implements AccessLogReceiver {
    private final Logger logger;

    public Slf4jLogReceiver() {
        this("access");
    }

    public Slf4jLogReceiver(String logger) {
        this(LoggerFactory.getLogger(logger));
    }

    public Slf4jLogReceiver(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void logMessage(String message) {
        logger.debug(message);
    }
}
