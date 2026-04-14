package com.innowise.apigateway.client;

import com.innowise.apigateway.config.UriConfig;
import com.innowise.apigateway.dto.AuthRequest;
import com.innowise.apigateway.exception.AuthRegistrationUserException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Component
public class AuthWebClient {

  private static final String REGISTER_PATH = "/auth/register";
  private static final String ROLLBACK_PATH = "/auth";
  private final WebClient webClient;

  public AuthWebClient(WebClient.Builder builder, UriConfig uriConfig) {
    this.webClient = builder
        .baseUrl(uriConfig.getAuthService())
        .build();
  }

  public Mono<Void> saveCredentials(AuthRequest request) {
    return webClient.post()
        .uri(REGISTER_PATH)
        .bodyValue(request)
        .retrieve()
        .onStatus(HttpStatusCode::isError, res -> Mono.error(new AuthRegistrationUserException("Auth failed")))
        .toBodilessEntity()
        .then();
  }

  public Mono<Void> rollback(String email) {
    return webClient.delete()
        .uri(ROLLBACK_PATH + "/{email}", email)
        .retrieve()
        .toBodilessEntity()
        .then();
  }
}
