package com.wilson.inkwell.apigateway.dto;

import java.util.Map;

public record ApiAggregationResponse(
    int status,
    Map<String, Object> body
) {
    
}
