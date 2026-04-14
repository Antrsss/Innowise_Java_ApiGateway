package com.innowise.apigateway.service;

import com.innowise.apigateway.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface RegistrationOrchestrator {
  Mono<ResponseEntity<String>> register(RegistrationDto dto);
}
