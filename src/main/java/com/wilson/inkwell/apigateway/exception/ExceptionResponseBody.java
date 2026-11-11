package com.wilson.inkwell.apigateway.exception;

import java.time.Instant;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

/**
 * Record that models the body of a http response error
 */
public record ExceptionResponseBody(
    int status,
    HttpErrorEnum error,
    String message,
    Instant timestamp
) {
    
}
