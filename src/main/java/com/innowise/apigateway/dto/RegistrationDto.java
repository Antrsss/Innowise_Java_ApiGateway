package com.innowise.apigateway.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationDto {
  private String password;
  private String role;
  private String email;
  private String name;
  private String surname;
  private LocalDate birthDate;
}
