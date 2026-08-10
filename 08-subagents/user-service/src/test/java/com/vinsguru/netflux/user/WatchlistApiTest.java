package com.vinsguru.netflux.user;

import com.vinsguru.netflux.user.dto.AddWatchlistRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, RsaKeyTestConfiguration.class})
@AutoConfigureRestTestClient
class WatchlistApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movie ids in watchlist")
    @Sql(statements = "INSERT INTO watchlist (user_id, movie_id) VALUES (9001, 123), (9001, 456)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM watchlist WHERE user_id = 9001", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMovieIdsInWatchlist() {
        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactlyInAnyOrder(123L, 456L);
    }

    @Test
    @DisplayName("Should add a movie to the watchlist")
    @Sql(statements = "DELETE FROM watchlist WHERE user_id = 9002", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAddMovieToWatchlist() {
        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9002")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(789L))
                .exchange()
                .expectStatus().isCreated();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9002")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactly(789L);
    }

    @Test
    @DisplayName("Should be idempotent when adding a duplicate movie")
    @Sql(statements = "INSERT INTO watchlist (user_id, movie_id) VALUES (9003, 111)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM watchlist WHERE user_id = 9003", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldBeIdempotentWhenAddingDuplicateMovie() {
        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9003")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(111L))
                .exchange()
                .expectStatus().isCreated();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9003")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactly(111L);
    }

    @Test
    @DisplayName("Should remove a movie from the watchlist")
    @Sql(statements = "INSERT INTO watchlist (user_id, movie_id) VALUES (9004, 222)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM watchlist WHERE user_id = 9004", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRemoveMovieFromWatchlist() {
        restTestClient.delete()
                .uri("/api/users/watchlist/{movieId}", 222)
                .header("X-User-Id", "9004")
                .exchange()
                .expectStatus().isNoContent();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9004")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).isEmpty();
    }

    @Test
    @DisplayName("Should be idempotent when removing a movie that is not in the watchlist")
    void shouldBeIdempotentWhenRemovingNonexistentMovie() {
        restTestClient.delete()
                .uri("/api/users/watchlist/{movieId}", 999)
                .header("X-User-Id", "9005")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when X-User-Id header is missing")
    void shouldReturn401WhenUserIdHeaderMissing() {
        var problem = restTestClient.get()
                .uri("/api/users/watchlist")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when X-User-Id header is malformed")
    void shouldReturn401WhenUserIdHeaderMalformed() {
        var problem = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "abc")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when add watchlist request is invalid")
    void shouldReturn400WhenAddWatchlistRequestInvalid() {
        var problem = restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "9006")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(null))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

}
