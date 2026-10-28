package com.wilson.inkwell.apigateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import com.wilson.inkwell.apigateway.route.handler.CustomerProfileQueryHandler;
import com.wilson.inkwell.apigateway.utils.ResourceName;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor 
public class RouteConfig {

    private final CustomerProfileQueryHandler customerProfileQueryHandler;

    @Value("${network.authority.name.authorizationservice}") 
    String authorizationServiceName;

    @Value("${network.authority.name.customerservice}") 
    String customerServiceName;

    @Value("${network.server.port.customerservice}")
    private int customerServicePort;

    @Value("${network.server.port.authorizationservice}")
    private int authorizationServicePort;

    /**
     * Configures a RouterFunction and builds a gatweway to
     * the Authorization Service.
     * 
     * @return RouterFunction for the authorization service
     */
    @Bean
    RouterFunction<ServerResponse> authorizationServiceRouting() {

        String BASE_PATH = "/" + 
                           ResourceName.API + 
                           "/" + 
                           ResourceName.AUTH + 
                           "/";

        return GatewayRouterFunctions
                .route("authorization-service") 
                // TODO I think I should just replace all these definitions with a single wild card */
                .route(RequestPredicates.POST(BASE_PATH + ResourceName.LOGIN), HandlerFunctions.http())
                .route(RequestPredicates.POST(BASE_PATH + ResourceName.CREATE_ACCOUNT), HandlerFunctions.http())
                .route(RequestPredicates.POST(BASE_PATH + ResourceName.REFRESH), HandlerFunctions.http())
                .route(RequestPredicates.POST(BASE_PATH + ResourceName.LOGOUT), HandlerFunctions.http())
                .route(RequestPredicates.GET(BASE_PATH + "/" + ResourceName.ME + "/" + ResourceName.EMAIL), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://" + authorizationServiceName + ":" + authorizationServicePort))

                .build();
    }

    @Bean
    RouterFunction<ServerResponse> customerProfileRouting() {

        String BASE_PATH = "/" + 
                           ResourceName.API + 
                           "/" + 
                           ResourceName.CUSTOMERS + 
                           "/" + 
                           ResourceName.ME + 
                           "/";

        return GatewayRouterFunctions
                .route("customer-profile-route")
                .route(RequestPredicates.GET(BASE_PATH + ResourceName.PROFILE), customerProfileQueryHandler)
                
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> customerAddressRouting() {

        String BASE_PATH = "/" + 
                           ResourceName.API + 
                           "/" + 
                           ResourceName.CUSTOMERS + 
                           "/" + 
                           ResourceName.ME + 
                           "/";

        return GatewayRouterFunctions
                .route("customer-address-route")
                
                // proxy the request
                .route(RequestPredicates.GET(BASE_PATH + ResourceName.ADDRESSES), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://" + customerServiceName + ":" + customerServicePort))
                .build();
    }

}
