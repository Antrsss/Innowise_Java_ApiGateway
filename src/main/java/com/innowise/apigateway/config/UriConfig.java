package com.innowise.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter @Setter
public class UriConfig {
  private String userService = "lb://user-service";
  private String authService = "lb://auth-service";
  private String orderService = "lb://order-service";
}
