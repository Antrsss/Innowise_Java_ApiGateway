package com.innowise.apigateway.filter;

import com.innowise.apigateway.config.UriConfig;
import com.innowise.apigateway.dto.UserDto;
import com.innowise.apigateway.exception.InvalidJwtUserException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

  private static final String VALIDATION_PATH = "/auth/validate";
  private static final String LOGIN_PATH = "/login";
  private static final String REGISTER_PATH = "/register";
  private static final String BEARER_TOKEN = "Bearer ";

  private final WebClient webClient;

  public JwtAuthFilter(WebClient.Builder builder, UriConfig uriConfig) {
    super(Config.class);
    this.webClient = builder
        .baseUrl(uriConfig.getAuthService())
        .build();
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String path = request.getURI().getPath();

      if (path.contains(LOGIN_PATH) || path.contains(REGISTER_PATH)) {
        return chain.filter(exchange);
      }

      String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if (authHeader != null && authHeader.startsWith(BEARER_TOKEN)) {
        String token = authHeader.substring(7);

        return validateTokenRemote(token)
            .flatMap(userInfo -> {
              ServerHttpRequest mutatedRequest = exchange.getRequest()
                  .mutate()
                  .header("X-User-Email", userInfo.getEmail())
                  .build();

              return chain.filter(exchange.mutate().request(mutatedRequest).build());
            })
            .onErrorResume(e -> {
              exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
              return exchange.getResponse().setComplete();
            });
      }

      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    };
  }

  private Mono<UserDto> validateTokenRemote(String token) {
    return webClient.post()
        .uri(VALIDATION_PATH)
        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN + token)
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            Mono.error(new InvalidJwtUserException("Token is invalid")))
        .bodyToMono(UserDto.class);
  }

  public static class Config {}
}