package com.wilson.inkwell.apigateway.exception;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

public class InvalidRequestParameter extends HttpCustomException{

    public InvalidRequestParameter(String message) {
        super(message, HttpErrorEnum.INVALID_PARAMETER_VALUE);
    }
    
}
