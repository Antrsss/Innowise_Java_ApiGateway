package com.innowise.apigateway.controller;

import com.innowise.apigateway.dto.RegistrationDto;
import com.innowise.apigateway.service.impl.RegistrationOrchestratorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationOrchestratorImpl orchestrator;

  @PostMapping("/register")
  public Mono<ResponseEntity<String>> register(@RequestBody @Valid RegistrationDto dto) {
    return orchestrator.register(dto);
  }
}