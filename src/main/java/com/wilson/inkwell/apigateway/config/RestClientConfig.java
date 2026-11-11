package com.wilson.inkwell.apigateway.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration 
public class RestClientConfig {
  
    @Bean
    RestClient customRestClient() {
        // docs here: https://docs.spring.io/spring-framework/reference/integration/rest-clients.html

        HttpClient httpClient = HttpClient.newBuilder()
                                          .connectTimeout(Duration.ofSeconds(3))
                                          .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                         .requestFactory(requestFactory) // jdk's HttpClient
                         .build();
    }

}
