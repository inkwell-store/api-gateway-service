package com.wilson.inkwell.apigateway.route;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

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
                .route(RequestPredicates.POST("/auth/login"), HandlerFunctions.http())
                .route(RequestPredicates.POST("/auth/create-account"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .before(BeforeFilterFunctions.rewritePath("/(?<segment>.*)", "/api/${segment}"))

                .build();
    }

}
