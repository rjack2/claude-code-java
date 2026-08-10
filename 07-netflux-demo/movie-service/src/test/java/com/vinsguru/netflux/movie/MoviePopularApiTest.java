package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class MoviePopularApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movies sorted by vote count descending using the default limit")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMoviesSortedByVoteCountDescending() {
        var results = fetchPopular("");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(4L, 2L, 1L, 3L, 5L);
    }

    @Test
    @DisplayName("Should respect the limit param")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRespectLimitParam() {
        var results = fetchPopular("?limit=2");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(4L, 2L);
    }

    @Test
    @DisplayName("Should silently clamp a limit below the minimum to 1")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampLimitBelowMinimum() {
        var results = fetchPopular("?limit=0");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(4L);
    }

    @Test
    @DisplayName("Should silently clamp a negative limit to 1")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampNegativeLimitToMinimum() {
        var results = fetchPopular("?limit=-5");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(4L);
    }

    @Test
    @DisplayName("Should silently clamp a limit above the maximum to 100")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampLimitAboveMaximum() {
        var results = fetchPopular("?limit=500");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(4L, 2L, 1L, 3L, 5L);
    }

    private List<MovieSummary> fetchPopular(String queryString) {
        return restTestClient.get()
                .uri("/api/movies/popular" + queryString)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }

}
