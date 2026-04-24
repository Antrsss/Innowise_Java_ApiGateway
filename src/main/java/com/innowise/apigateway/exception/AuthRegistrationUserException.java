package com.innowise.apigateway.exception;

import org.springframework.http.HttpStatus;

public class AuthRegistrationUserException extends BaseUserException {
  public AuthRegistrationUserException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
