package com.wilson.inkwell.apigateway.exception;

public class ServiceTimedoutException extends RuntimeException{

    public ServiceTimedoutException(String message) {
        super(message);
    }
    
}
