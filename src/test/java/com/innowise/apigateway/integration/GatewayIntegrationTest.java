package com.innowise.apigateway.integration;

import com.innowise.apigateway.dto.RegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class GatewayIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  private RegistrationDto registrationDto;

  @BeforeEach
  void setUp() {
    registrationDto = new RegistrationDto();
    registrationDto.setEmail("test@innowise.com");
    registrationDto.setPassword("password123");
    registrationDto.setRole("ROLE_USER");
    registrationDto.setName("Ivan");
    registrationDto.setSurname("Ivanov");
    registrationDto.setBirthDate(LocalDate.of(1995, 5, 20));
  }

  @Test
  void register_FullSuccess_Returns201() {
    stubFor(post(urlEqualTo("/auth/register"))
        .willReturn(aResponse().withStatus(201)));

    stubFor(post(urlEqualTo("/api/users"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"id\": 1, \"email\": \"test@innowise.com\"}")));

    webTestClient.post()
        .uri("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(registrationDto)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(String.class).isEqualTo("User fully registered");
  }

  @Test
  void register_UserServiceFails_TriggersRollback() {
    stubFor(post(urlEqualTo("/auth/register"))
        .willReturn(aResponse().withStatus(201)));

    stubFor(post(urlEqualTo("/api/users"))
        .willReturn(aResponse().withStatus(500)));

    stubFor(delete(urlPathMatching("/auth/.*"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.post()
        .uri("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(registrationDto)
        .exchange()
        .expectStatus().is5xxServerError();

    verify(deleteRequestedFor(urlPathMatching("/auth/.*")));
  }

  @Test
  void securedRoute_WithoutToken_Returns401() {
    webTestClient.get()
        .uri("/api/users/1")
        .exchange()
        .expectStatus().isUnauthorized();
  }
}