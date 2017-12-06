package com.davinryan.common.restservice.domain.response;

import java.io.Serializable;

public class Response implements Serializable {

    private ResponseStatus status;

    private transient Object payload;

    public Response(ResponseStatus status, Object payload) {
        this.status = status;
        this.payload = payload;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", payload=" + payload +
                '}';
    }

    public enum ResponseStatus {
        SUCCESS("Success"),
        FAILURE("Failure");

        private final String statusDescription;

        ResponseStatus(String statusDescription) {
            this.statusDescription = statusDescription;
        }

        public String getStatusDescription() {
            return statusDescription;
        }
    }
}
