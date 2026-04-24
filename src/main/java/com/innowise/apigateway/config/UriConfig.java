package com.innowise.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services-routes")
@Getter @Setter
public class UriConfig {
  private String userService;
  private String authService;
  private String orderService;
}
