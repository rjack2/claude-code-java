package com.vinsguru.netflux.user;

import com.vinsguru.netflux.user.dto.LoginRequest;
import com.vinsguru.netflux.user.dto.LoginResponse;
import com.vinsguru.netflux.user.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, RsaKeyTestConfiguration.class})
@AutoConfigureRestTestClient
class AuthApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should register a new user")
    @Sql(statements = "DELETE FROM users WHERE username = 'auth-register-user'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegisterNewUser() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("auth-register-user", "password123", "Register User"))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @DisplayName("Should return 409 ProblemDetail when username already exists")
    @Sql(statements = "DELETE FROM users WHERE username = 'auth-conflict-user'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturn409WhenUsernameAlreadyTaken() {
        var request = new RegisterRequest("auth-conflict-user", "password123", "Conflict User");

        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        var problem = restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should login successfully and return a token")
    @Sql(statements = "DELETE FROM users WHERE username = 'auth-login-user'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldLoginSuccessfully() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("auth-login-user", "password123", "Login User"))
                .exchange()
                .expectStatus().isCreated();

        var response = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("auth-login-user", "password123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.token()).isNotBlank();
        assertThat(response.name()).isEqualTo("Login User");
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when password is incorrect")
    @Sql(statements = "DELETE FROM users WHERE username = 'auth-badpass-user'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturn401WhenPasswordIncorrect() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("auth-badpass-user", "password123", "Bad Pass User"))
                .exchange()
                .expectStatus().isCreated();

        var problem = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("auth-badpass-user", "wrong-password"))
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when username does not exist")
    void shouldReturn401WhenUsernameNotFound() {
        var problem = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("no-such-user", "password123"))
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when register request is invalid")
    void shouldReturn400WhenRegisterRequestInvalid() {
        var problem = restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when login request is invalid")
    void shouldReturn400WhenLoginRequestInvalid() {
        var problem = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 409 ProblemDetail when concurrent registrations race for the same username")
    @Sql(statements = "DELETE FROM users WHERE username = 'auth-race-user'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturn409WhenConcurrentRegistrationsRace() throws Exception {
        var request = new RegisterRequest("auth-race-user", "password123", "Race User");
        var barrier = new CyclicBarrier(2);
        Callable<Integer> registerTask = () -> {
            barrier.await();
            var result = restTestClient.post()
                    .uri("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .exchange()
                    .expectBody(Void.class)
                    .returnResult();
            return result.getStatus().value();
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            var futures = executor.invokeAll(List.of(registerTask, registerTask));
            var statuses = futures.stream()
                    .map(AuthApiTest::getStatus)
                    .toList();

            assertThat(statuses).containsExactlyInAnyOrder(201, 409);
        } finally {
            executor.shutdown();
        }
    }

    private static Integer getStatus(Future<Integer> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

}
