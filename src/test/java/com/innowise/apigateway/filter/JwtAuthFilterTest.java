package com.innowise.apigateway.filter;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class JwtAuthFilterTest {

  @Autowired
  private WebTestClient webTestClient;

  private static final String TEST_TOKEN = "valid-token";
  private static final String TEST_EMAIL = "test@innowise.com";

  @BeforeEach
  void setUp() {
    WireMock.reset();
  }

  @Test
  void filter_Success_ShouldForwardEmailHeader() {
    stubFor(post(urlEqualTo("/auth/validate"))
        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + TEST_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"email\": \"" + TEST_EMAIL + "\"}")));

    stubFor(get(urlEqualTo("/api/users/me"))
        .withHeader("X-User-Email", equalTo(TEST_EMAIL))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/api/users/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_TOKEN)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void filter_InvalidToken_ShouldReturn401() {
    stubFor(post(urlEqualTo("/auth/validate"))
        .willReturn(aResponse().withStatus(401)));

    webTestClient.get()
        .uri("/api/users/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_TOKEN)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void filter_MissingHeader_ShouldReturn401() {
    webTestClient.get()
        .uri("/api/users/me")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void filter_SkipForLogin_ShouldPassWithoutToken() {
    stubFor(post(urlEqualTo("/auth/login"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.post()
        .uri("/auth/login")
        .exchange()
        .expectStatus().isOk();
  }
}