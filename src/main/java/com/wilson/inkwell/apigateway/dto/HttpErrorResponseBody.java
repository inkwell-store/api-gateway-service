package com.wilson.inkwell.apigateway.dto;

import java.time.Instant;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

public record HttpErrorResponseBody(
    int status,
    HttpErrorEnum error,
    String message,
    Instant timestramp
) {
    
}
