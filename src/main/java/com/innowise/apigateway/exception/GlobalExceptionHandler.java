package com.innowise.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String MESSAGE_KEY = "message";

  @ExceptionHandler(BaseUserException.class)
  public ResponseEntity<Object> handleBaseException(BaseUserException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put(TIMESTAMP_KEY, LocalDateTime.now());
    body.put(MESSAGE_KEY, e.getMessage());

    return new ResponseEntity<>(body, e.getStatus());
  }
}
