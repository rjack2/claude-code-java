package com.vinsguru.netflux.recommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final RestClient.Builder builder;

    public RestClientConfig(RestClient.Builder builder) {
        this.builder = builder;
    }

    @Bean
    public RestClient movieServiceRestClient(MovieServiceProperties properties) {
        return builder.baseUrl(properties.baseUrl()).build();
    }

    @Bean
    public RestClient userServiceRestClient(UserServiceProperties properties) {
        return builder.baseUrl(properties.baseUrl()).build();
    }

}
