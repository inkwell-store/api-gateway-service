package com.wilson.inkwell.apigateway.exception;

public class InvalidRequestParameter extends RuntimeException{

    public InvalidRequestParameter(String message) {
        super(message);
    }
    
}
