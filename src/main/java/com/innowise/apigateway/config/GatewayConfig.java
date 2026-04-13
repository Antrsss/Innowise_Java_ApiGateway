package com.innowise.apigateway.config;

import com.innowise.apigateway.filter.JwtAuthenticationFilter;
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

  private final JwtAuthenticationFilter jwtFilter;
  private final UriConfig uriConfig;

  @Bean
  public RouteLocator servicesRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("auth-service", p -> p
            .path("/auth/**")
            .uri(uriConfig.getAuthService()))

        .route("user-service", p -> p
            .path("/api/users/**")
            .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
            .uri(uriConfig.getUserService()))

        .route("card-service", p -> p
            .path("/api/cards/**")
            .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
            .uri(uriConfig.getUserService()))

        .route("order-service", p -> p
            .path("/api/orders/**")
            .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
            .uri(uriConfig.getOrderService()))

        .build();
  }
}
