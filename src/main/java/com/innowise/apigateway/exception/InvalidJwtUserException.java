package com.innowise.apigateway.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtUserException extends BaseUserException {
  public InvalidJwtUserException(String message) {
    super(HttpStatus.UNAUTHORIZED, message);
  }
}
