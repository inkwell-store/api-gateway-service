package com.wilson.inkwell.apigateway.exception;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

/**
 * Indicates an http request to a service failed and the request 
 * didn't even complete, for example, due to a connection failure or
 * the service being down.
 */
public class ServiceUnavailableException extends HttpCustomException{

    public ServiceUnavailableException(String message) {
        super(message, HttpErrorEnum.SERVICE_UNAVAILABLE);
    }
    
}
