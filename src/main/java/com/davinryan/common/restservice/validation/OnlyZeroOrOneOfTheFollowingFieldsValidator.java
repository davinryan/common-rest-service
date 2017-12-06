package com.davinryan.common.restservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

import static com.davinryan.common.restservice.reflection.ReflectionUtils.invokeGetterMethod;

/**
 * This validator will test given a group of {@link Field} objects that only one is not null.
 */
public class OnlyZeroOrOneOfTheFollowingFieldsValidator extends BaseFieldsValidator implements ConstraintValidator<OnlyZeroOrOneOfTheFollowingFields, Object> {

    @Override
    public void initialize(OnlyZeroOrOneOfTheFollowingFields constraintAnnotation) {
        addFields(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        List<Field> listOfFieldsThatHaveData = new ArrayList<Field>();
        for (Field singleField : getSingleFields()) {
            Object fieldValue = invokeGetterMethod(value, singleField.name());
            if (fieldValue != null) {
                listOfFieldsThatHaveData.add(singleField);
            }
        }

        for (Field compositeField : getCompositeFields()) {
            for (String fieldName : compositeField.compositeFieldNames()) {
                Object fieldValue = invokeGetterMethod(value, fieldName);
                if (fieldValue != null) {
                    listOfFieldsThatHaveData.add(compositeField);
                    break;
                }
            }
        }
        return listOfFieldsThatHaveData.size() <= 1;
    }
}
