package com.wilson.inkwell.apigateway.route;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

    @Bean
    RouterFunction<ServerResponse> authorizationServiceRouting() {
        return GatewayRouterFunctions
                .route("authorization-service")
                .route(
                    RequestPredicates.path("/public-hello"),
                    HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.rewritePath("/public-hello", "/api/auth/public-hello"))
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .build();
    }

}

