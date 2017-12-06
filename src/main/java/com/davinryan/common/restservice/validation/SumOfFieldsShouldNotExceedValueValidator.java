package com.davinryan.common.restservice.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;

import static com.davinryan.common.restservice.reflection.ReflectionUtils.invokeGetterMethod;

/**
 * This validator will test given a group of {@link Field} objects that only one is not null. This validator only works
 * with annotations that have a fields {@link Field[]} and max {@link String} attribute.
 */
public class SumOfFieldsShouldNotExceedValueValidator extends BaseFieldsValidator implements ConstraintValidator<Annotation, Object> {

    private double maxValue;

    @Override
    public void initialize(Annotation constraintAnnotation) {

        // Using reflection provides support for different annotations pointing to this same validator.
        Method fieldsMethod = org.springframework.util.ReflectionUtils.findMethod(constraintAnnotation.getClass(), "fields");
        org.springframework.util.ReflectionUtils.makeAccessible(fieldsMethod);
        Field[] fields = (Field[])org.springframework.util.ReflectionUtils.invokeMethod(fieldsMethod, constraintAnnotation);
        addFields(fields);

        Method maxMethod = org.springframework.util.ReflectionUtils.findMethod(constraintAnnotation.getClass(), "max");
        org.springframework.util.ReflectionUtils.makeAccessible(maxMethod);
        maxValue = Double.valueOf((String)org.springframework.util.ReflectionUtils.invokeMethod(maxMethod, constraintAnnotation));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        double total = 0;

        for (Field singleField : getSingleFields()) {
            // We have a collection
            if (StringUtils.isNotBlank(singleField.subFieldName())) {
                Object fieldObj = invokeGetterMethod(value, singleField.name());
                Collection collection = (Collection) fieldObj;
                for (Object collectionObj : collection) {
                    total += getNumberValue(collectionObj, singleField.subFieldName());
                }
            }
            // We have a number object
            else {
                total += getNumberValue(value, singleField.name());
            }
        }

        for (Field compositeField : getCompositeFields()) {
            throw new IllegalArgumentException("Composite fields are not supported for this validation annoation type. Use single field only.");
        }
        return total <= maxValue;
    }

    /**
     * Gets the double value of a number type with fieldname on object obj
     */
    private double getNumberValue(Object objWithField, String fieldName) {
        Object fieldObj = invokeGetterMethod(objWithField, fieldName);
        if (fieldObj != null) {
            return convertToDouble(fieldObj);
        }
        return 0;
    }

    private double convertToDouble(Object fieldValue) {
        if (fieldValue instanceof Integer) {
            Integer value = (Integer) fieldValue;
            return value.doubleValue();
        } else if (fieldValue instanceof Double) {
            return (Double) fieldValue;
        } else if (fieldValue instanceof Float) {
            Float value = (Float) fieldValue;
            return value.doubleValue();
        } else if (fieldValue instanceof BigDecimal) {
            BigDecimal value = (BigDecimal) fieldValue;
            return value.doubleValue();
        } else {
            throw new IllegalArgumentException("fieldValue: " + fieldValue + " is not one of java.lang.Integer, java.lang.Double, java.lang.Float or java.math.BigDecimal");
        }
    }
}
