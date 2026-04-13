package com.innowise.apigateway.filter;

import com.innowise.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    super(Config.class);
    this.jwtUtil = jwtUtil;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String path = request.getURI().getPath();

      if (path.contains("/login") || path.contains("/register")) {
        return chain.filter(exchange);
      }

      String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if (!authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization Header");
      }

      String token = authHeader.substring(7);
      try {
        jwtUtil.validateToken(token);
      } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
      }

      return chain.filter(exchange);
    };
  }

  public static class Config {}
}
