package com.innowise.apigateway.client;

import com.innowise.apigateway.config.UriConfig;
import com.innowise.apigateway.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserWebClient {

  private static final String USERS_API = "/api/users";
  private final WebClient webClient;

  public UserWebClient(WebClient.Builder builder, UriConfig uriConfig) {
    this.webClient = builder
        .baseUrl(uriConfig.getUserService())
        .build();
  }

  public Mono<UserDto> createProfile(UserDto dto) {
    return webClient.post()
        .uri(USERS_API)
        .bodyValue(dto)
        .retrieve()
        .bodyToMono(UserDto.class);
  }
}
