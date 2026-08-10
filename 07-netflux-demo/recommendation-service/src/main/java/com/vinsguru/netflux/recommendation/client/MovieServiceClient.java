package com.vinsguru.netflux.recommendation.client;

import com.vinsguru.netflux.recommendation.dto.MovieSummary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MovieServiceClient {

    private final RestClient restClient;

    public MovieServiceClient(RestClient movieServiceRestClient) {
        this.restClient = movieServiceRestClient;
    }

    public List<MovieSummary> getPopular(int limit) {
        return fetch("/api/movies/popular?limit={limit}", limit);
    }

    public List<MovieSummary> getLatest(int limit) {
        return fetch("/api/movies/latest?limit={limit}", limit);
    }

    public List<MovieSummary> searchByGenre(String genre, int limit) {
        return fetch("/api/movies/search?genre={genre}&limit={limit}", genre, limit);
    }

    public List<MovieSummary> getByIds(List<Long> ids) {
        return fetch("/api/movies/batch?ids={ids}", ids);
    }

    private List<MovieSummary> fetch(String uri, Object... uriVariables) {
        return restClient.get()
                .uri(uri, uriVariables)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
