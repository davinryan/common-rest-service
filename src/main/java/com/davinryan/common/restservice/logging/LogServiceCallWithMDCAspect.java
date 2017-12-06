package com.davinryan.common.restservice.logging;

import com.davinryan.common.restservice.domain.request.Request;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.lang.annotation.AnnotationFormatError;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Add this class as a bean instance in your spring context and add the {@link LogServiceCallWithMDC} annotation to your public
 * service method if you want to log using the Mapped Diagnostic Context or MDC.
 *
 * WARNING: this annoation only works with public methods who have only a single parameter of type
 * {@link Request}.
 */
@Aspect
public class LogServiceCallWithMDCAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceCallWithMDCAspect.class);
    private static final String CORRELATION_ID = "cid";
    private static final String TYPE = "type";

    private static final ThreadLocal<Stack<String>> OPERATION_STACK = new ThreadLocal<Stack<String>>() {
        @Override
        protected Stack<String> initialValue() {
            return new Stack<String>();
        }

    };

    @Pointcut(value = "execution(public * *(..))")
    public void anyPublicMethod() {
        // This method is empty because it simply provides a location for @Pointcut to locate public methods.
    }

    @Around("anyPublicMethod() && @annotation(logServiceCallWithMDC)") //NOSONAR
    public Object logAction(ProceedingJoinPoint pjp, LogServiceCallWithMDC logServiceCallWithMDC) throws Throwable { // NOSONAR
        // Do what you want with the join point arguments
        int requestCount = 0;
        for (Object object : pjp.getArgs()) {
            if (requestCount > 1) {
                throw new AnnotationFormatError("You can't have more than one Request object per service call!");
            }
            if (object instanceof Request) {
                requestCount++;
                logBegin(LOGGER, (Request) object, "Received " + object.getClass().getName() + " request");
            }
        }

        return pjp.proceed();
    }

    @After("anyPublicMethod() && @annotation(logServiceCallWithMDC)")
    public void afterLogAction(LogServiceCallWithMDC logServiceCallWithMDC) { //NOSONAR
        logEnd(LOGGER);
    }

    public static void logBegin(Logger logger, Request request, String message) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("operation", message);
        logBegin(logger, request, parameters);
    }

    /**
     * Sets the Correlation ID for the current thread, writes a BEGIN operation to the logOperation, and pushes the operation onto
     * the stack.
     */
    private static void logBegin(Logger logger, Request operation, Map<String, String> parameters) {
        String cidExpression = String.format("cid='%s' ", operation.getCorrelationId() == null ? "unspecified" : operation.getCorrelationId());
        MDC.put(CORRELATION_ID, cidExpression);
        String operationName = operation.getClass().getSimpleName();
        logOperation(logger, "BEGIN " + operationName, parameters);
        OPERATION_STACK.get().push(operationName);
    }

    /**
     * Pops the last operation off the stack and logs an END operation.
     */
    public static void logEnd(Logger logger) {
        logOperation(logger, "END " + OPERATION_STACK.get().pop() + "'}", null);
        if (OPERATION_STACK.get().isEmpty()) {
            MDC.remove(CORRELATION_ID);
            MDC.remove(TYPE);
        }
    }

    private static void logOperation(Logger logger, String operation, Map<String, String> parameters) {
        logOperation(null, logger, operation, parameters);
    }

    private static void logOperation(Marker marker, Logger logger, String operation, Map<String, String> parameters) {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        if (parameters != null) {
            properties = new LinkedHashMap<String, String>(parameters);
        }
        properties.put("operation", "'" + operation + "'");
        logger.info(marker, properties.toString());
    }


}
