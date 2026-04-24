package com.innowise.apigateway.exception;

import org.springframework.http.HttpStatus;

public class UserProfileCreationUserException extends BaseUserException {
  public UserProfileCreationUserException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }
}
