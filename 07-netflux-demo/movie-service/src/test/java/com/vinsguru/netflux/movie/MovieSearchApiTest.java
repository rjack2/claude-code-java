package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class MovieSearchApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return 400 ProblemDetail when neither title nor genre is provided")
    void shouldReturn400WhenNeitherTitleNorGenreProvided() {
        var problem = restTestClient.get()
                .uri("/api/movies/search")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should match title with a partial case-insensitive search")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterByTitlePartialCaseInsensitive() {
        var results = search("title=ADVENTURE");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    @DisplayName("Should match genre exactly and case-sensitively")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterByGenreExactCaseSensitive() {
        var results = search("genre=Action");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 3L, 4L);
    }

    @Test
    @DisplayName("Should not match genre when case differs")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotMatchGenreWithDifferentCase() {
        var results = search("genre=action");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should combine title and genre with AND logic")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCombineTitleAndGenreWithAnd() {
        var results = search("title=adventure&genre=Action");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    @DisplayName("Should silently clamp the limit param")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampLimit() {
        var results = search("genre=Action&limit=2");

        assertThat(results).hasSize(2);
    }

    private List<MovieSummary> search(String queryString) {
        return restTestClient.get()
                .uri("/api/movies/search?" + queryString)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }

}
