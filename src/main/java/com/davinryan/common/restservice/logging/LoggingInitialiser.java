package com.davinryan.common.restservice.logging;

import com.davinryan.common.restservice.jee.JeeUtils;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * ServletContextListener for initialising logging.
 * <p>
 * Looks in web.xml for serviceName and then uses this to look for a JNDI variable called [serviceName].debug. If this
 * is present and set to true then logging is initialised from log4j-debug.xml instead of the normal log4j.xml.
 * <p>
 * This class also writes start and stop messages to the logs (because Websphere's logging only goes to System Out).
 *
 */
public class LoggingInitialiser implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInitialiser.class.getName());

    private static final String SERVICE_NAME = "serviceName";

    private static final String LOG4J2_DEBUG_XML = "log4j2-debug.xml";

    private static final JeeUtils jeeUtils = new JeeUtils();

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // get service name from web.xml
        String serviceName = getServiceName(event);
        if (serviceName == null) {
            LOGGER.error(SERVICE_NAME + " is not defined");
        } else {
            // check whether debugging is enabled for this service
            if (Boolean.parseBoolean(jeeUtils.jndiLookup(serviceName + ".debug", "false"))) {
                // Try to load log4j2 first
                LoggerContext log4j2Context = Configurator.initialize(null, LOG4J2_DEBUG_XML);

                // If that fails then try to load log4j second
                if (log4j2Context.getConfiguration().getConfigurationSource().getLocation() == null) {
                    LOGGER.error(LOG4J2_DEBUG_XML + " not found");
                }
            }
        }
        LOGGER.info("{} starting - version={}", serviceName, null);
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("{} stopping", getServiceName(event));
    }

    private static String getServiceName(ServletContextEvent event) {
        return event.getServletContext().getInitParameter(SERVICE_NAME);
    }
}
