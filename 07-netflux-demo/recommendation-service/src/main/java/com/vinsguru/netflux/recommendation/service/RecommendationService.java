package com.vinsguru.netflux.recommendation.service;

import com.vinsguru.netflux.recommendation.client.MovieServiceClient;
import com.vinsguru.netflux.recommendation.client.UserServiceClient;
import com.vinsguru.netflux.recommendation.config.MovieServiceProperties;
import com.vinsguru.netflux.recommendation.dto.HomeResponse;
import com.vinsguru.netflux.recommendation.dto.MovieSummary;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final MovieServiceClient movieServiceClient;
    private final UserServiceClient userServiceClient;
    private final MovieServiceProperties movieServiceProperties;

    public RecommendationService(MovieServiceClient movieServiceClient,
                                 UserServiceClient userServiceClient,
                                 MovieServiceProperties movieServiceProperties) {
        this.movieServiceClient = movieServiceClient;
        this.userServiceClient = userServiceClient;
        this.movieServiceProperties = movieServiceProperties;
    }

    public HomeResponse getHomeRecommendations(String userId) {
        var limit = movieServiceProperties.limit();
        var watchlistIds = userServiceClient.getWatchlist(userId);
        var watchlist = watchlistIds.isEmpty()
                ? List.<MovieSummary>of()
                : movieServiceClient.getByIds(watchlistIds);
        var popular = movieServiceClient.getPopular(limit);
        var latest = movieServiceClient.getLatest(limit);
        var byGenre = movieServiceProperties.genres().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        genre -> movieServiceClient.searchByGenre(genre, limit),
                        (a, b) -> a,
                        LinkedHashMap::new));

        return new HomeResponse(watchlist, popular, latest, byGenre);
    }

}
