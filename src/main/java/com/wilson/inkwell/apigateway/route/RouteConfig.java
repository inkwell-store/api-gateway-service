package com.wilson.inkwell.apigateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

    @Value("${network.authority.name.authorizationservice}") 
    String authorizationServiceName;

    @Value("${network.authority.name.customerservice}") 
    String customerServiceName;

    /**
     * Configures a RouterFunction and builds a gatweway to
     * the Authorization Service.
     * 
     * @return RouterFunction for the authorization service
     */
    @Bean
    RouterFunction<ServerResponse> authorizationServiceRouting() {
        return GatewayRouterFunctions
                .route("authorization-service") 
                .route(RequestPredicates.POST("/api/auth/login"), HandlerFunctions.http())
                .route(RequestPredicates.POST("/api/auth/create-account"), HandlerFunctions.http())
                .route(RequestPredicates.POST("/api/auth/refresh"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://" + authorizationServiceName + ":8081"))
                // .before(BeforeFilterFunctions.rewritePath("/(?<segment>.*)", "${segment}"))

                .build();
    }

    /**
     * Configures a RouterFunction and builds a gatweway to the Customer Service.
     * 
     * @return RouterFunction for the customer service
     */
    @Bean
    RouterFunction<ServerResponse> customerServiceRouting() {
        return GatewayRouterFunctions
                .route("customer-service")
                .route(
                    // any request to this resource is by definition matched and 
                    // authenticated, so I believe I can omit http method matchers
                    RequestPredicates.path("/api/customer/me/**"), 
                    HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri("http://" + customerServiceName + ":8083"))
                // .before(BeforeFilterFunctions.rewritePath("/(?<segment>.*)", "${segment}"))
                .build();
    }

}
