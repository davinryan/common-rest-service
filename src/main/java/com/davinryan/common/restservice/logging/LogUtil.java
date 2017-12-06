package com.davinryan.common.restservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging util that pretty logs for development.
 */
public class LogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class.getName());

    /**
     * Private constructor to hide public one.
     */
    private LogUtil() {
    }

    public static void logDebugMsg(Logger logger, String title, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("##########################");
            logger.debug(title);
            logger.debug("##########################");
            logger.debug(message);
        }
    }
}
