package com.davinryan.common.restservice.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * A JMS Queue Publisher for submitting Form XML Body messages onto an MQ Queue.
 * <p>
 * The binding between the java:comp/env name and the global JNDI name (as injected by the {@code @Resource()})<br>
 * annotation is performed in the ibm-ejb-jar-bnd.xml deployment decriptor file.
 * <p>
 */
public class JmsQueuePublisher implements MessagePublisher {

    /**
     * Logging instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsQueuePublisher.class.getName());

    /**
     * The MQ Queue Connection Factory as setup in the application server
     */
    private QueueConnectionFactory queueConnectionFactory;

    /**
     * The MQ Queue as setup in the application server
     */
    private Queue destination;

    /**
     * The JMS connection to MQ
     */
    private QueueConnection connection;

    /**
     * Constructs a new QueuePublisher.
     */
    public JmsQueuePublisher(QueueConnectionFactory queueConnectionFactory, Queue dest) {
        this.queueConnectionFactory = queueConnectionFactory;
        this.destination = dest;
        init();
    }

    /**
     * Initialises the connection
     */
    private synchronized void init() {
        if (null == connection) {
            try {
                connection = this.queueConnectionFactory.createQueueConnection("mqm", "");
            } catch (JMSException e) {
                LOGGER.error("Error creating JMS Connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Clean up the connection
     */
    public synchronized void tearDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOGGER.error("Error closing JMS Connection: ", e.getMessage(), e);
            }
            connection = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see nz.co.acc.egateway.common.messaging.JMSClientLocal#send()
     */
    @Override
    public synchronized void send(String text, String jmsType, String sendingAppPropertyName,
                                  String sendingAppPropertyNameValue) throws JMSException {
        if (null == connection) {
            throw new JMSException("Connection is null, and is unlikely to have been initialised");
        }

        QueueSession session = null;

        try {
            session = createSession(connection);

            QueueSender messageSender = createMessageSender(session);

            TextMessage message = createMessage(text, jmsType, sendingAppPropertyName, sendingAppPropertyNameValue, session);

            // Set ACC specific JMSType and Sending Application
            LOGGER.info("#####################################");
            LOGGER.info("Sending JMS message to Queue");
            LOGGER.info("#####################################");
            LOGGER.info("Message Properties: [type='" + jmsType + "'] [sendingAppPropertyName='" + sendingAppPropertyName + "'] [sendingAppPropertyNameValue='" + sendingAppPropertyNameValue + "']");

            sendMessage(messageSender, message);
            // Finalise messageSender.
            messageSender.close();

        } catch (JMSException e) {
            LOGGER.error("Error sending to MQ: ", e.getMessage());
            throw e;
        } finally {
            closeSession(session);
        }
    }

    private void closeSession(QueueSession session) {
        if (session != null) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Closing session");
                }
                session.close();
            } catch (JMSException e) {
                LOGGER.error("Error closing MQ session: ", e.getMessage(), e);
            }
        }
    }

    @Override
    public void healthCheck() throws JMSException {
        if (null == connection) {
            throw new JMSException("Connection is null, and is unlikely to have been initialised");
        }

        QueueSession session = null;

        try {
            session = createSession(connection);
        } catch (JMSException e) {
            LOGGER.error("Error sending to MQ: ", e.getMessage());
            throw e;
        } finally {
            closeSession(session);
        }
    }

    private QueueSession createSession(QueueConnection connection) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating transactional JMS session");
        }
        // Transactional session = true
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating persistant message messageSender with TimeToLive=0");
        }
        return session;
    }

    private QueueSender createMessageSender(QueueSession session) throws JMSException {
        // Set persistent and TTL of zero (default)
        QueueSender messageSender = session.createSender(destination);
        messageSender.setDeliveryMode(DeliveryMode.PERSISTENT);
        messageSender.setTimeToLive(0);
        return messageSender;
    }

    private void sendMessage(QueueSender messageSender, TextMessage message) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending message");
        }
        // Send message
        messageSender.send(message);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Message sent. Closing messageSender");
        }
    }

    private TextMessage createMessage(String text, String jmsType, String sendingAppPropertyName, String sendingAppPropertyNameValue, QueueSession session) throws JMSException {
        // Create message
        // WARNING - DO NOT use createTextMessage(text) to set message. It gives
        // different results depending on installed WMQ versions. An IBM bug, who'd have thought :)
        TextMessage message = session.createTextMessage();
        // setText seems to work consistently...
        message.setText(text);
        message.setJMSType(jmsType);
        message.setStringProperty(sendingAppPropertyName, sendingAppPropertyNameValue);
        return message;
    }
}
