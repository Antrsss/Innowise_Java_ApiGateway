package com.innowise.apigateway.config;

import com.innowise.apigateway.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UriConfig.class)
@RequiredArgsConstructor
public class GatewayConfig {

  private static final String AUTH_PATH = "/auth/**";
  private static final String USERS_PATH = "/api/users/**";
  private static final String CARDS_PATH = "/api/cards/**";
  private static final String ORDERS_PATH = "/api/orders/**";

  private final JwtAuthFilter jwtFilter;
  private final UriConfig uriConfig;

  @Bean
  public RouteLocator servicesRoutes(RouteLocatorBuilder builder) {
    var authFilter = jwtFilter.apply(new JwtAuthFilter.Config());

    return builder.routes()
        .route("auth-service", p -> p
            .path(AUTH_PATH)
            .uri(uriConfig.getAuthService()))

        .route("user-service", p -> p
            .path(USERS_PATH)
            .filters(f -> f.filter(authFilter))
            .uri(uriConfig.getUserService()))

        .route("card-service", p -> p
            .path(CARDS_PATH)
            .filters(f -> f.filter(authFilter))
            .uri(uriConfig.getUserService()))

        .route("order-service", p -> p
            .path(ORDERS_PATH)
            .filters(f -> f.filter(authFilter))
            .uri(uriConfig.getOrderService()))

        .build();
  }
}
