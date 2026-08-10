package com.vinsguru.netflux.recommendation.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient userServiceRestClient) {
        this.restClient = userServiceRestClient;
    }

    public List<Long> getWatchlist(String userId) {
        return restClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
