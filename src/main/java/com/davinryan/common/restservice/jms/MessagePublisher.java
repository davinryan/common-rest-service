package com.davinryan.common.restservice.jms;

import javax.jms.JMSException;

/**
 * Interface for sending messages
 *
 * @author Mike Hepburn
 */
public interface MessagePublisher {

    /**
     * Send a Form message (XML as String)
     *
     * @throws JMSException
     */
    void send(String form, String jmsType, String sendingAppPropertyName, String sendingAppPropertyNameValue) throws JMSException;

    /**
     * This method will check that MQ is still alive.
     */
    void healthCheck() throws JMSException;
}
