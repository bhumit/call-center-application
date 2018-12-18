package com.callcenter.exception;

import org.springframework.http.HttpStatus;

public class CallCenterServiceException extends RuntimeException {

    private HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public CallCenterServiceException(String message) {
        super(message);
    }

    public CallCenterServiceException(String message, Throwable ex) {
        super(message, ex);
    }

    public CallCenterServiceException(String message, HttpStatus errorStatus) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public HttpStatus getStatus() {
        return errorStatus;
    }


}
