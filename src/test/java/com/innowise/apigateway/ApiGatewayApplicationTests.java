package com.innowise.apigateway;

import com.innowise.apigateway.config.UriConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableConfigurationProperties(UriConfig.class)
class ApiGatewayApplicationTests {

  @Test
  void contextLoads() {
  }

}
