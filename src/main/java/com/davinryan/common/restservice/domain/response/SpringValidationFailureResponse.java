package com.davinryan.common.restservice.domain.response;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

;

/**
 * Useful class for constructing meaningful responses that indicated what validation errors occurred.
 */
public class SpringValidationFailureResponse extends Response {

    public SpringValidationFailureResponse(BindingResult bindingResult) {
        super(ResponseStatus.FAILURE, generatePayload(bindingResult));
    }

    private static List<String> generatePayload(BindingResult bindingResult) {
        Iterator<ObjectError> itr = bindingResult.getAllErrors().iterator();
        List<String> errors = new ArrayList<String>();
        errors.add("Failed with validation errors:");
        while (itr.hasNext()) {
            StringBuilder sb = new StringBuilder();
            Object errorObj = itr.next();
            if (errorObj instanceof FieldError) {
                FieldError error = (FieldError) errorObj;
                sb.append("Field: '").append(error.getField())
                        .append("' with value '").append(error.getRejectedValue()).append("' was rejected with reason ")
                        .append("'").append(error.getDefaultMessage()).append("'");
                errors.add(sb.toString());
            } else if (errorObj instanceof ObjectError) {
                ObjectError error = (ObjectError) errorObj;
                sb.append("Field: '").append(error.getObjectName())
                        .append("' with value '").append(error.getDefaultMessage()).append("' was rejected with reason ")
                        .append("'").append(error.getDefaultMessage()).append("'");
                errors.add(sb.toString());
            } else {
                sb.append("General Error: '").append(errorObj);
                errors.add(sb.toString());
            }
        }
        return errors;
    }
}
