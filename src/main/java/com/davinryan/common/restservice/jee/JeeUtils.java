package com.davinryan.common.restservice.jee;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Utility to help make container tasks easier.
 */
public class JeeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JeeUtils.class.getName());

    public static Connection getOracleConnection(DataSource dataSource) {
        return null;
    }
//    /**
//     * If running on IBM WAS returns a native {@link Connection} from {@code dataSource} {@link DataSource} otherwise
//     * a wrapped OracleConnection is returned. This is needed for {@link XMLType} activities.
//     * <p>
//     * WARNING: if you are getting the error in the logs just ignore it because the OracleConnection tries to run close() but this fails as this is managed by the container
//     * because it isn't following JDBC 4.0 standards.
//     * "J2CA0216I: The Connection Manager received a fatal connection error from the Resource Adapter for resource jdbc/egateway."
//     *
//     * @return the {@link Connection} from the {@code dataSource} {@link DataSource} in this {@code
//     * JdbcFormSubmissionDao} instance.
//     * @throws SQLException
//     */
//    public Connection getOracleConnection(DataSource dataSource) throws SQLException {
//        Connection connection;
//        if (runningInIBMWASContainer()) {
//            LOGGER.debug("We are running on IBM so using WSCallHelper.getNativeConnection to get a connection");
//            connection = WSCallHelper.getNativeConnection(dataSource.getConnection());
//        } else {
//            // Required for working with oracle XML type
//            LOGGER.debug("We are running on a normal JEE server so using getOracleConnection.unwrap(OracleConnection.class)");
//            connection = dataSource.getConnection().unwrap(OracleConnection.class);
//        }
//        return connection;
//    }

//    /**
//     * Useful method to work out if we are running in an IBM WAS EE server or not. This will drive what
//     * SAXParserFactory, DocumentBuilderFactory, TranformerFactory or Oracleconnection you load at run time.
//     * @return true if in an IBM container
//     */
//    public Boolean runningInIBMWASContainer() {
//    /*
//     * We know that IBM uses their own forked version of the JVM which doesn't use the following SAX parser. So
//     * this is a cheap and easy way of checking if we are using IBM
//     * @return
//     */
//        if (runningInIBMWASContainer == null) {
//            try {
//                Class.forName("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", false, Thread.currentThread().getContextClassLoader());
//                runningInIBMWASContainer = false;
//            } catch (ClassNotFoundException e) { // NOSONAR
//                runningInIBMWASContainer = true;
//            }
//        }
//        return runningInIBMWASContainer;
//    }

    /**
     * Magic function that can get JNDI value from any JEE container e.g. tomcat or IBM etc...
     * @param name jndi name (no prefixes pleases, just the name)
     * @param defaultValue default value if jndi value cannot be found
     * @param <T> Object type
     * @return Jndi value as {@code <T>}
     */
    public static <T> T jndiLookup(String name, T defaultValue) {
        LOGGER.debug("Attempting to find jndi parameter " + name);

        if (StringUtils.isBlank(name)) {
            return defaultValue;
        }
        try {
            InitialContext context = new InitialContext();
            T value = jndiLookup(context, "java:comp/env/" + name);
            if (value == null) {
                value = jndiLookup(context, name);
                if (value == null) {
                    LOGGER.error("Giving up finding jndi parameter, I'm out of ideas. This must be an IBM product :(");
                }
            }
            LOGGER.info("{} = {}", name, value);
            if (value != null) {
                return value;
            }
        } catch (NamingException e) { //NOSONAR
            LOGGER.error("Failed to load JNDI parameter: " + name + ". Using '" + defaultValue + "' instead.");
        } catch (Throwable e2) { //NOSONAR - we catch this as InitialContext throws NoClassDefFoundError when running unit tests.
            // This will never happen in production, and if it does its logged.
            LOGGER.error("Failed to load JNDI parameter: " + name + ". Using '" + defaultValue + "' instead."); //NOSONAR
        }
        return defaultValue;
    }

    private static <T> T jndiLookup(InitialContext context, String jndiName) {
        T value = null;
        try {
            value = (T) context.lookup(jndiName);
            LOGGER.debug("Found it. JndiName '" + jndiName + "' worked.");
        } catch (NamingException e) {
            LOGGER.debug("Failed to find jndi parmeter '" + jndiName + "'", e);
        }
        return value;
    }
}
