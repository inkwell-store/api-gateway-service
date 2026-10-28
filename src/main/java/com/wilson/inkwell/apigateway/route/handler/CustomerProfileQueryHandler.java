package com.wilson.inkwell.apigateway.route.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.wilson.inkwell.apigateway.exception.InvalidRequestParameter;
import com.wilson.inkwell.apigateway.service.CustomerProfileAggregatorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
@RequiredArgsConstructor
public final class CustomerProfileQueryHandler implements HandlerFunction<ServerResponse> {

    private final CustomerProfileAggregatorService customerProfileAggregatorService;

    private final String PROFILE_PARAM_NAME = "attributes";

    private final Set<String> AUTH_SERVICE_ATTRIBUTES = Set.of(
            "email");

    private final Set<String> CUSTOMER_SERVICE_ATTRIBUTES = Set.of(
            "first_name",
            "last_name",
            "photo",
            "date_of_birth");

    @Override
    public ServerResponse handle(ServerRequest request) throws Exception {

        Map<String, Object> body = new HashMap<>();
        Optional<String> paramListAsString = request.param(PROFILE_PARAM_NAME);
        Set<String> requestCustomerAttribures = new HashSet<>();
        Set<String> requestAuthenticationAttributes = new HashSet<>();

        // since attributes are optional, in the case of their absence I fetch the whole
        // resource by calling all services
        if (paramListAsString.isEmpty() || paramListAsString.get().isEmpty()) {

            requestCustomerAttribures.addAll(CUSTOMER_SERVICE_ATTRIBUTES);
            requestAuthenticationAttributes.addAll(AUTH_SERVICE_ATTRIBUTES);

            log.debug("Will fetch entire profile resource because no attributes were provided");

        } else {

            // if the attributes are present I need to determine if they are all valid
            for (String attributeValue : paramListAsString.get().split(",")) {
                if (!AUTH_SERVICE_ATTRIBUTES.contains(attributeValue)
                        && !CUSTOMER_SERVICE_ATTRIBUTES.contains(attributeValue)) {

                    throw new InvalidRequestParameter(
                            "Parameter value " + attributeValue + " not supported for profile resource");

                } else if (AUTH_SERVICE_ATTRIBUTES.contains(attributeValue)) {
                    requestAuthenticationAttributes.add(attributeValue);
                } else if (CUSTOMER_SERVICE_ATTRIBUTES.contains(attributeValue)) {
                    requestCustomerAttribures.add(attributeValue);
                }
            }

        }

        // call the aggregator method
        body = customerProfileAggregatorService.getProfile(
            request, 
            requestCustomerAttribures, 
            requestAuthenticationAttributes
        );

        return ServerResponse.status(HttpStatus.ACCEPTED).body(body);
    }

}
