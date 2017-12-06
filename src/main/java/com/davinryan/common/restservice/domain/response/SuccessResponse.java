package com.davinryan.common.restservice.domain.response;

/**
 * Created by RyanDa on 12/04/2016.
 */
public class SuccessResponse extends Response {

    public SuccessResponse(Object payload) {
        super(ResponseStatus.SUCCESS, payload);
    }
}
