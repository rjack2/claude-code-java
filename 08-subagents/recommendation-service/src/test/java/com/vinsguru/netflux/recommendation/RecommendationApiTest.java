package com.vinsguru.netflux.recommendation;

import com.vinsguru.netflux.recommendation.config.MovieServiceProperties;
import com.vinsguru.netflux.recommendation.dto.HomeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@AutoConfigureMockRestServiceServer
class RecommendationApiTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private MovieServiceProperties movieServiceProperties;

    @Test
    @DisplayName("Should return home recommendations aggregated from movie-service and user-service")
    void shouldReturnHomeRecommendations() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/users/watchlist")))
                .andRespond(withSuccess("[1, 2]", MediaType.APPLICATION_JSON));

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/movies/batch?ids=1%2C2")))
                .andRespond(withSuccess(moviesJson(1, 2), MediaType.APPLICATION_JSON));

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/movies/popular?limit=20")))
                .andRespond(withSuccess(moviesJson(3), MediaType.APPLICATION_JSON));

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/movies/latest?limit=20")))
                .andRespond(withSuccess(moviesJson(4), MediaType.APPLICATION_JSON));

        for (var genre : movieServiceProperties.genres()) {
            mockServer.expect(method(HttpMethod.GET))
                    .andExpect(requestTo(containsString("/api/movies/search?genre=" + encode(genre) + "&limit=20")))
                    .andRespond(withSuccess(moviesJson(5), MediaType.APPLICATION_JSON));
        }

        var response = restTestClient.get()
                .uri("/api/recommendations/home")
                .header("X-User-Id", "42")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HomeResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.watchlist()).hasSize(2);
        assertThat(response.popular()).hasSize(1);
        assertThat(response.latest()).hasSize(1);
        assertThat(response.byGenre()).hasSize(movieServiceProperties.genres().size());
        assertThat(response.byGenre().get("Action")).hasSize(1);
        mockServer.verify();
    }

    @Test
    @DisplayName("Should return empty watchlist without calling movie-service batch endpoint")
    void shouldReturnEmptyWatchlistWithoutCallingBatch() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/users/watchlist")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/movies/popular?limit=20")))
                .andRespond(withSuccess(moviesJson(3), MediaType.APPLICATION_JSON));

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("/api/movies/latest?limit=20")))
                .andRespond(withSuccess(moviesJson(4), MediaType.APPLICATION_JSON));

        for (var genre : movieServiceProperties.genres()) {
            mockServer.expect(method(HttpMethod.GET))
                    .andExpect(requestTo(containsString("/api/movies/search?genre=" + encode(genre) + "&limit=20")))
                    .andRespond(withSuccess(moviesJson(5), MediaType.APPLICATION_JSON));
        }

        var response = restTestClient.get()
                .uri("/api/recommendations/home")
                .header("X-User-Id", "42")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HomeResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.watchlist()).isEmpty();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when X-User-Id header is missing")
    void shouldReturn401WhenUserIdHeaderMissing() {
        var problem = restTestClient.get()
                .uri("/api/recommendations/home")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
        mockServer.verify();
    }

    private static String moviesJson(long... ids) {
        return LongStream.of(ids)
                .mapToObj(id -> """
                        {"id": %d, "title": "Movie %d", "posterPath": "/poster.jpg", "backdropPath": "/backdrop.jpg", "voteAverage": 7.5, "releaseDate": "2024-01-01", "genres": ["Action"]}
                        """.formatted(id, id))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

}
