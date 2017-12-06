package com.davinryan.common.restservice.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Collection;

import static com.davinryan.common.restservice.reflection.ReflectionUtils.invokeGetterMethod;

/**
 * This validator will test given a group of {@link Field} objects that only one is not null.
 */
public class SumOfTimeFieldsShouldNotExceedValueValidator extends BaseFieldsValidator implements ConstraintValidator<SumOfTimeFieldsShouldNotExceedValue, Object> {

    public static final String MINUTES = "minutes";
    private static final String HOURS = "hours";
    private double maxValue;

    private String maxValueUnits;

    @Override
    public void initialize(SumOfTimeFieldsShouldNotExceedValue constraintAnnotation) {
        addFields(constraintAnnotation.fields());
        maxValue = Double.valueOf(constraintAnnotation.max());
        maxValueUnits = constraintAnnotation.maxUnits();
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
                    total += getDoubleValue(collectionObj, singleField.subFieldName(), singleField.units());
                }
            }
            // Total by Double
            else {
                total += getDoubleValue(value, singleField.name(), singleField.units());
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
    private double getDoubleValue(Object objWithField, String fieldName, String fieldValueUnits) {
        Object fieldObj = invokeGetterMethod(objWithField, fieldName);
        if (fieldObj != null) {
            return convertToDouble(fieldObj, fieldValueUnits);
        }
        return 0;
    }

    private double convertToDouble(Object fieldValue, String fieldValueUnits) {
        Double finalValue;
        if (fieldValue instanceof Integer) {
            Integer value = (Integer) fieldValue;
            finalValue = value.doubleValue();
        } else if (fieldValue instanceof Double) {
            finalValue = (Double) fieldValue;
        } else if (fieldValue instanceof Float) {
            Float value = (Float) fieldValue;
            finalValue = value.doubleValue();
        } else if (fieldValue instanceof BigDecimal) {
            BigDecimal value = (BigDecimal) fieldValue;
            finalValue = value.doubleValue();
        } else {
            throw new IllegalArgumentException("fieldValue: " + fieldValue + " is not one of java.lang.Integer, java.lang.Double, java.lang.Float or java.math.BigDecimal");
        }
        if (maxValueUnits.equals(MINUTES)) {
            if (fieldValueUnits.equals(MINUTES)) {
                return finalValue;
            } else if (fieldValueUnits.equals(HOURS)) {
                return finalValue * 60;
            }
        }
        return finalValue;
    }
}
