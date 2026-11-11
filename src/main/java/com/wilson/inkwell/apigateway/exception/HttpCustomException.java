package com.wilson.inkwell.apigateway.exception;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

/**
 * Base class to model exceptions thrown during http requests.
 */
public class HttpCustomException  extends RuntimeException{

    private final HttpErrorEnum errorType;

    public HttpCustomException(String message, HttpErrorEnum errorType) {
        super(message);
        this.errorType = errorType;
    }

    public HttpErrorEnum getErrorType() {
        return errorType;
    }
    
}
