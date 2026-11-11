package com.wilson.inkwell.apigateway.route.filter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.web.servlet.function.ServerRequest;

import com.wilson.inkwell.apigateway.exception.InvalidRequestParameter;
import com.wilson.inkwell.apigateway.route.utils.QueryParameters;

public class ProfileQueryParameterFilter implements Function<ServerRequest, ServerRequest>{

    @Override
    public ServerRequest apply(ServerRequest request) {

        // check if the parameter exists at all
        Optional<String> paramListAsString = request.param("attributes");
        Map<String, Set<String>> allowedParams = QueryParameters.getProfileResourceAllowedParameters();

        // if not attach all values
        if(paramListAsString.isEmpty()) {
            request.attributes().putAll(allowedParams);
            return request;
        }
        
        // check if their values are correct
        for(String value : paramListAsString.get().split(",")) {
            if(!allowedParams.get("attributes").contains(value)) {
                throw new InvalidRequestParameter(
                            "Parameter value: " + value + " not supported for resource: profile ");
            }
        }

        return request;
    }
    
}
