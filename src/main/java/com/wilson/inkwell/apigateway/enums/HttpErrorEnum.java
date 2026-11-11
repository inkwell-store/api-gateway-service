package com.wilson.inkwell.apigateway.enums;

/**
 * Enum that maps different type errors that might happen
 * during http requests.
 */
public enum HttpErrorEnum {

    ACCESS_TOKEN_INVALID("ACCESS_TOKEN_INVALID"),
    INVALID_PARAMETER_VALUE("INVALID_PARAMETER_VALUE"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    UNMAPPED_ERROR("UNMAPPED_ERROR");

    private String value;

    private HttpErrorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HttpErrorEnum getErrorFrom(String value) {

        if (value == null) {
            return UNMAPPED_ERROR;
        }

        for (HttpErrorEnum error : HttpErrorEnum.values()) {
            if (error.value.equals(value)) {
                return error;
            }
        }
        
        return UNMAPPED_ERROR;
    }

}
