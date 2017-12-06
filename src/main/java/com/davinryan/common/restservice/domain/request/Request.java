package com.davinryan.common.restservice.domain.request;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Object for All requests made to this service.
 */
public class Request implements Serializable {

    @ApiModelProperty(value = "An ID to track this transaction throughout the system.", required = true)
    @NotBlank
    @Size(max = 255)
    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "correlationId='" + correlationId + '\'' +
                '}';
    }
}
