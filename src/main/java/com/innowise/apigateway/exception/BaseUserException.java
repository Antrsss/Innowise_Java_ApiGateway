package com.innowise.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseUserException extends RuntimeException {

  private final HttpStatus status;

  public BaseUserException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }
}
