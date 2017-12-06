package com.davinryan.common.restservice.validation;

import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents any field. typically used in composition with other validation annotations.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Field {
    String message() default "Fields cannot co-exist together";
    Class <?>[] groups() default {};
    Class <?extends Payload>[] payload() default{};

    /**
     * Name of the field to use
     * @return The name of the field.
     */
    String name() default "";

    /**
     * Name of the sub field to use in the case that 'name' refers to a {@link java.util.Collection} object.
     *
     * @return The name of the sub field name.
     */
    String subFieldName() default "";

    /**
     * Units if this cannot be derived from Java Type. E.g. minutes, hours for time related queries.
     * @return
     */
    String units() default "";

    /**
     * @return names of fields that are validated together as a composite field. Useful when treating many
     * fields as one.
     */
    String[] compositeFieldNames() default {};
}
