package com.wilson.inkwell.apigateway.exception;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

public class ServiceTimedoutException extends HttpCustomException{

    public ServiceTimedoutException(String message, HttpErrorEnum errorType) {
        super(message, errorType);
    }
    
}
