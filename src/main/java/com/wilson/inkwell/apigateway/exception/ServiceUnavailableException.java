package com.wilson.inkwell.apigateway.exception;

public class ServiceUnavailableException extends RuntimeException{

    public ServiceUnavailableException(String message) {
        super(message);
    }
    
}
