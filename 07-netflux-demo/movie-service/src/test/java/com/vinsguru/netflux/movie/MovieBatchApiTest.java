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
class MovieBatchApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movie summaries for the given ids")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnSummariesForGivenIds() {
        var results = restTestClient.get()
                .uri("/api/movies/batch?ids=1,2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Should return only found ids when some ids do not exist")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnOnlyFoundIdsWhenSomeDoNotExist() {
        var results = restTestClient.get()
                .uri("/api/movies/batch?ids=1,999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when ids param is missing")
    void shouldReturn400WhenIdsParamMissing() {
        var problem = restTestClient.get()
                .uri("/api/movies/batch")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

}
