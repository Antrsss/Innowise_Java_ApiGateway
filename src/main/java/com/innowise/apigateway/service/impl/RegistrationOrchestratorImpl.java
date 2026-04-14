package com.innowise.apigateway.service.impl;

import com.innowise.apigateway.client.AuthWebClient;
import com.innowise.apigateway.client.UserWebClient;
import com.innowise.apigateway.dto.AuthRequest;
import com.innowise.apigateway.dto.RegistrationDto;
import com.innowise.apigateway.dto.UserDto;
import com.innowise.apigateway.entity.Role;
import com.innowise.apigateway.exception.UserProfileCreationUserException;
import com.innowise.apigateway.service.RegistrationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegistrationOrchestratorImpl implements RegistrationOrchestrator {

  private final AuthWebClient authWebClient;
  private final UserWebClient userWebClient;

  @Override
  public Mono<ResponseEntity<String>> register(RegistrationDto dto) {
    AuthRequest authReq = new AuthRequest(
        dto.getEmail(),
        dto.getPassword(),
        Role.valueOf(dto.getRole())
    );

    UserDto userDto = new UserDto();
    userDto.setEmail(dto.getEmail());
    userDto.setName(dto.getName());
    userDto.setSurname(dto.getSurname());
    userDto.setBirthDate(dto.getBirthDate());
    userDto.setActive(true);

    return authWebClient.saveCredentials(authReq)
        .then(userWebClient.createProfile(userDto)
            .onErrorResume(e ->
                authWebClient.rollback(dto.getEmail())
                    .then(Mono.error(
                        new UserProfileCreationUserException("Profile creation failed, rolled back credentials")
                    ))
            )
        )
        .map(res -> ResponseEntity.status(HttpStatus.CREATED).body("User fully registered"));
  }
}
