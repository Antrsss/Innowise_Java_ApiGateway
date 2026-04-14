package com.innowise.apigateway.service;

import com.innowise.apigateway.client.AuthWebClient;
import com.innowise.apigateway.client.UserWebClient;
import com.innowise.apigateway.service.impl.RegistrationOrchestratorImpl;
import com.innowise.apigateway.dto.RegistrationDto;
import com.innowise.apigateway.dto.UserDto;
import com.innowise.apigateway.exception.UserProfileCreationUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationOrchestratorImplTest {

  @Mock
  private AuthWebClient authWebClient;
  @Mock
  private UserWebClient userWebClient;

  @InjectMocks
  private RegistrationOrchestratorImpl orchestrator;

  private RegistrationDto dto;

  @BeforeEach
  void setUp() {
    dto = new RegistrationDto();
    dto.setEmail("test@test.com");
    dto.setPassword("password123");
    dto.setRole("ROLE_USER");
    dto.setName("Darya");
    dto.setSurname("Z");
    dto.setBirthDate(LocalDate.of(2000, 1, 1));
  }

  @Test
  void register_Success() {
    when(authWebClient.saveCredentials(any())).thenReturn(Mono.empty());
    when(userWebClient.createProfile(any())).thenReturn(Mono.just(new UserDto()));

    Mono<ResponseEntity<String>> result = orchestrator.register(dto);

    StepVerifier.create(result)
        .expectNextMatches(response -> response.getStatusCode() == HttpStatus.CREATED)
        .verifyComplete();
  }

  @Test
  void register_UserCreationFails_TriggersRollback() {
    when(authWebClient.saveCredentials(any())).thenReturn(Mono.empty());
    when(userWebClient.createProfile(any())).thenReturn(Mono.error(new RuntimeException("DB Error")));
    when(authWebClient.rollback(dto.getEmail())).thenReturn(Mono.empty());

    Mono<ResponseEntity<String>> result = orchestrator.register(dto);

    StepVerifier.create(result)
        .expectError(UserProfileCreationUserException.class)
        .verify();

    verify(authWebClient).rollback(dto.getEmail());
  }
}
