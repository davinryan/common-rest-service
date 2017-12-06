package com.davinryan.common.restservice.logging;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.davinryan.common.restservice.reflection.ReflectionUtils.findGetterMethod;
import static com.davinryan.common.restservice.reflection.ReflectionUtils.findSetterMethod;
import static com.davinryan.common.restservice.reflection.ReflectionUtils.invokeGetterMethod;
import static com.davinryan.common.restservice.reflection.ReflectionUtils.invokeSetterMethod;

/**
 * Utility class that can scan any object that uses {@link RedactWhenLogging} and Redact those fields as long as they
 * use a supported type.
 */
public class RedactUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class.getName());

    private RedactUtil() {}


    /**
     * Will redact fields that are annotated with {@link RedactWhenLogging}.
     *
     * @param objectToRedact this objects fields will be scanned for the {@link RedactWhenLogging} annotation.
     *                       <p>
     *                       WARNING: doesn't support redacting inherited fields at the moment.
     */
    public static <T> T redactObject(T objectToRedact) {
        if (objectToRedact == null) {
            return null;
        }
        for (Field field : objectToRedact.getClass().getDeclaredFields()) {
            Class fieldType = field.getType();
            String fieldName = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (objectToRedact instanceof Collection || objectToRedact.getClass().isArray()) {
                redactCollection(objectToRedact);
            } else {
                redactField(objectToRedact, fieldType, fieldName, annotations);
            }

            // Iterate over children
            if (findGetterMethod(objectToRedact, fieldName) != null) {
                Object childObjectToRedact = invokeGetterMethod(objectToRedact, fieldName);
                if (childObjectToRedact != null) {
                    redactObject(childObjectToRedact);
                }
            }
        }
        return objectToRedact;
    }

    /**
     * Will redact fields that are annotated with {@link RedactWhenLogging} for a {@link Collection} or array.
     *
     * @param objectToRedact this objects fields will be scanned for the {@link RedactWhenLogging} annotation.
     *                       <p>
     *                       WARNING: doesn't support redacting inherited fields at the moment.
     */
    public static <T> T redactCollection(T objectToRedact) {
        if (objectToRedact == null) {
            return null;
        }
        if (objectToRedact instanceof Collection) {
            Collection objectToRedactCollection = (Collection) objectToRedact;
            for (Object objectToRedactItem : objectToRedactCollection) {
                redactObject(objectToRedactItem);
            }
        } else {
            redactObject(objectToRedact);
        }
        return objectToRedact;
    }

    private static <T> void redactField(T objectContainingField, Class fieldType, String fieldName, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == RedactWhenLogging.class && fieldType == String.class) {
                if (findSetterMethod(objectContainingField, fieldName, fieldType) != null) {
                    invokeSetterMethod(objectContainingField, fieldName, "REDACTED");
                }
            }
            else if (annotation.annotationType() == RedactWhenLogging.class && fieldType == DateTime.class) {
                if (findSetterMethod(objectContainingField, fieldName, fieldType) != null) {
                    invokeSetterMethod(objectContainingField, fieldName, null);
                }
            }
            else if (annotation.annotationType() == RedactWhenLogging.class && fieldType == LocalDate.class) {
                if (findSetterMethod(objectContainingField, fieldName, fieldType) != null) {
                    invokeSetterMethod(objectContainingField, fieldName, null);
                }
            }
            else if (annotation.annotationType() == RedactWhenLogging.class) {
                LOGGER.warn("Failed to Redact field '" + fieldName + "'. This field's fieldType of '" + fieldType
                        + "' is not supported by annotation " + RedactWhenLogging.class
                        + ". Field types that are supported are: [" + String.class +"," + DateTime.class + "," + LocalDate.class + "]. Either remove the annotation" +
                        " or change the field to use a supported fieldType.");
            }
        }
    }
}
