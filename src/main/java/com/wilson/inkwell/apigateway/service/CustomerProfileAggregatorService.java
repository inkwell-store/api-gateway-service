package com.wilson.inkwell.apigateway.service;

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

import com.wilson.inkwell.apigateway.dto.ApiAggregationResponse;
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

        Set<CompletableFuture<ApiAggregationResponse>> queries = new HashSet<>();

        // The endpoint is authenticated using spring security, if the request reaches
        // this point I can know for certain the authorization header is present and
        // it has a value, so I just pass the token to these requests.
        String accessToken = request.headers().header(AUTHORIZATION_HEADER).getFirst();

        // if any customer attributes are present
        if (!customerAttributes.isEmpty()) {

            queries.add(CompletableFuture.supplyAsync(()-> {
                log.info("Calling customer-service...");
                return customerServiceRequest(customerAttributes, accessToken);
            }));

        }

        // if any auth attributes are present
        if (!authAttributes.isEmpty()) {

            queries.add(CompletableFuture.supplyAsync(()-> {
                log.info("Calling authorization-service...");
                return authorizationServiceRequest(accessToken);
            }));
        }

        // converting the set to an array (weird syntax)
        CompletableFuture.allOf(queries.toArray(new CompletableFuture[0])); 

        try {

            // Here I wait for the responses
            for(CompletableFuture<ApiAggregationResponse> response : queries) {

                // here I neeed to inspect the response's body.
                // if something goes wrong in any of these requests I should return the error
                // as the body immediately. The first error that is found will be the one returned.

                // the request was successful in the range of 2XX
                if(String.valueOf(response.get().status()).startsWith("2")) {
                    responseBoby.putAll(response.get().body());
                } else {

                    log.debug("A request returned status:" + response.get().status());

                    // if the response is not successful then clear the body, add the error and retunr
                    responseBoby.clear();
                    responseBoby.putAll(response.get().body());
                    break;
                }
                
            }
        
        } catch (CancellationException | CompletionException | InterruptedException | ExecutionException e) {
            // find a way to map these exceptions to a proper response
            throw new RuntimeException(e.getMessage());
        }

        return responseBoby;
    }

    /**
     * Calls an http get request to authorization-service to fetch attributes for a customer's profile 
     */
    private ApiAggregationResponse authorizationServiceRequest(String bearerToken) {
        
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
                         .exchange((req, res) -> {

                            return new ApiAggregationResponse(
                                res.getStatusCode().value(),
                                res.bodyTo(new ParameterizedTypeReference<Map<String, Object>>() {})
                            );

                         });

    }

    /**
     * Calls an http get request to customer-service to fetch attributes for a customer's profile 
     */
    private ApiAggregationResponse customerServiceRequest(Set<String> profileAttributes, String bearerToken) {

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
                                                  .build())
                         .header(AUTHORIZATION_HEADER, bearerToken)
                         .accept(MediaType.APPLICATION_JSON)
                         .exchange((req, res) -> {

                            return new ApiAggregationResponse(
                                res.getStatusCode().value(),
                                res.bodyTo(new ParameterizedTypeReference<Map<String, Object>>() {})
                            );

                         });

    }

}
