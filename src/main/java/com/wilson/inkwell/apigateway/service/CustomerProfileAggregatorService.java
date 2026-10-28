package com.wilson.inkwell.apigateway.service;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.ServerRequest;

import com.wilson.inkwell.apigateway.utils.ResourceName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor
public class CustomerProfileAggregatorService {

    private final RestClient restClient;
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String PROFILE_PARAM_NAME = "attributes";

    @Value("${network.authority.name.authorizationservice}")
    private String authorizationServiceHost; 

    @Value("${network.authority.name.customerservice}")
    private String customerServiceHost; 

    @Value("${network.server.port.authorizationservice}")
    private int authorizationServicePort;

    @Value("${network.server.port.customerservice}")
    private int customerServicePort;

    /**
     * Fetches a profile resource, filtering by any attributes provided. If the request involves
     * calling multiple services, the request are run in parallel using CompletableFutures and, if
     * any of them fail it returns an error response.
     */
    public Map<String, Object> getProfile(ServerRequest request, Set<String> customerAttributes,
            Set<String> authAttributes) {

        Map<String, Object> responseBoby = new HashMap<>();

        Set<CompletableFuture<Map<String, Object>>> queries = new HashSet<>();

        // The endpoint is authenticated using spring security, if the request reaches
        // this point I can know for certain the authorization header is present and
        // it has a value, so I just pass the token to these requests.
        String accessToken = request.headers().header(AUTHORIZATION_HEADER).getFirst();

        // if any customer attributes are present
        if (!customerAttributes.isEmpty()) {

            log.info("Calling customer-service...");

            queries.add(CompletableFuture.supplyAsync(()-> {
                return customerServiceRequest(customerAttributes, accessToken);
            }));

        }

        // if any auth attributes are present
        if (!authAttributes.isEmpty()) {

            log.info("Calling authorization-service...");

            queries.add(CompletableFuture.supplyAsync(()-> {
                return authorizationServiceRequest(customerAttributes, accessToken);
            }));
        }

        // converting the set to an array (weird syntax)
        CompletableFuture.allOf(queries.toArray(new CompletableFuture[0])); 

        try {

            // Here I wait for the responses
            for(CompletableFuture<Map<String, Object>> response : queries) {
                responseBoby.putAll(response.get());
            }
        
        } catch (CancellationException | CompletionException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }

        return responseBoby;
    }

    private Map<String, Object> authorizationServiceRequest(Set<String> authAttributes, String bearerToken) {
        
        String path = "/" +
                      ResourceName.API +
                      "/" +
                      ResourceName.AUTH +
                      "/" +
                      ResourceName.ME +
                      "/" +
                      ResourceName.EMAIL;

        return restClient.get()
                         .uri((builder) -> builder.path(path)
                                                  .port(authorizationServicePort)
                                                  .host(authorizationServiceHost)
                                                  .scheme("http")
                                                  .build()
                         )
                         .header(AUTHORIZATION_HEADER, bearerToken)
                         .accept(MediaType.APPLICATION_JSON)
                         .retrieve()
                         .body(new ParameterizedTypeReference<Map<String, Object>>() {});

    }

    private Map<String, Object> customerServiceRequest(Set<String> profileAttributes, String bearerToken) {

        String path = "/" +
                      ResourceName.API +
                      "/" +
                      ResourceName.CUSTOMERS +
                      "/" +
                      ResourceName.ME +
                      "/" +
                      ResourceName.PROFILE;

        return restClient.get()
                         .uri((builder) -> builder.path(path)
                                                  .port(customerServicePort)
                                                  .host(customerServiceHost)
                                                  .scheme("http")
                                                  .queryParam(PROFILE_PARAM_NAME, String.join(",", profileAttributes))
                                                  .build()
                         )
                         .header(AUTHORIZATION_HEADER, bearerToken)
                         .accept(MediaType.APPLICATION_JSON)
                         .retrieve()
                         .body(new ParameterizedTypeReference<Map<String, Object>>() {});

    }

}
