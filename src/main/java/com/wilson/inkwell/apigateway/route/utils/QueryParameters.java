package com.wilson.inkwell.apigateway.route.utils;

import java.util.Map;
import java.util.Set;

public final class QueryParameters {
    
    public static Map<String, Set<String>> getProfileResourceAllowedParameters() {
        return Map.of(
            "attributes", Set.of("email","first_name", "last_name", "photo", "date_of_birth")
        );
    }

}
