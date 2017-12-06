package com.davinryan.common.restservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * TODO: provider better validation error messages.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = OnlyZeroOrOneOfTheFollowingFieldsValidator.class)
@Documented
public @interface OnlyZeroOrOneOfTheFollowingFields {
    String message() default "OnlyZeroOrOneOfTheFollowingFields validation failed on this object. Look at @OnlyZeroOrOneOfTheFollowingFields annotation at top of class for more details.";
    Class <?>[] groups() default {};
    Class <?extends Payload>[] payload() default{};
    Field[] fields() default {};

    /**
     * Defines several <code>@FieldMatch</code> annotations on the same element.
     *
     * @see OnlyZeroOrOneOfTheFollowingFields
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        OnlyZeroOrOneOfTheFollowingFields[] value();
    }
}
