package com.innowise.apigateway;

import com.innowise.apigateway.filter.JwtAuthenticationFilter;
import com.innowise.apigateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private GatewayFilterChain chain;

  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(jwtUtil);
  }

  @Test
  void shouldAllowLoginWithoutToken() {
    MockServerHttpRequest request = MockServerHttpRequest.get("/auth/login").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    Mono<Void> result = filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain);

    StepVerifier.create(result).verifyComplete();
    verifyNoInteractions(jwtUtil);
  }

  @Test
  void shouldThrowUnauthorizedIfHeaderMissing() {
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/1").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain).block();
    });

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Missing Authorization Header", exception.getReason());
  }
}