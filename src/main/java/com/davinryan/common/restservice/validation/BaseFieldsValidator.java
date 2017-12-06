package com.davinryan.common.restservice.validation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.List;

/**
 * This validator will test given a group of {@link Field} objects that only one is not null.
 */
public abstract class BaseFieldsValidator {

    /**
     * {@link Field} objects that represent a single field
     */
    private List<Field> singleFields = new ArrayList<Field>();

    /**
     * {@link Field} objects that represent a collection of fields that act as one.
     */
    private List<Field> compositeFields = new ArrayList<Field>();

    protected void addFields(Field... fields) {
        for (Field field : fields) {
            boolean isCompositeField = field.compositeFieldNames().length > 0;
            boolean isSingleField = StringUtils.isNotBlank(field.name());

            if (isCompositeField && isSingleField) {
                throw new AnnotationFormatError("name and compositeFields cannot be set at the same time. Please use one or the other");
            } else if (isSingleField) {
                singleFields.add(field);
            } else if (isCompositeField) {
                compositeFields.add(field);
            }
        }
    }

    protected List<Field> getSingleFields() {
        return singleFields;
    }

    protected List<Field> getCompositeFields() {
        return compositeFields;
    }
}
