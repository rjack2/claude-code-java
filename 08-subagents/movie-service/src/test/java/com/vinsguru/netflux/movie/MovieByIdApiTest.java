package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class MovieByIdApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return full movie details when the movie exists")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMovieDetailsWhenFound() {
        var movie = restTestClient.get()
                .uri("/api/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .returnResult()
                .getResponseBody();

        assertThat(movie.id()).isEqualTo(1L);
        assertThat(movie.title()).isEqualTo("The Great Adventure");
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(2020, 5, 1));
        assertThat(movie.genres()).containsExactlyInAnyOrder("Action", "Adventure");
        assertThat(movie.themes()).containsExactly("Heroism");
    }

    @Test
    @DisplayName("Should return 404 ProblemDetail when the movie does not exist")
    void shouldReturn404WhenNotFound() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getTitle()).isEqualTo("Movie Not Found");
    }

}
