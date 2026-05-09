package com.innowise.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationDto {

  @NotBlank(message = "Password is required")
  private String password;

  private String role;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 50)
  private String name;

  @NotBlank(message = "Surname is required")
  private String surname;

  @NotNull(message = "Birth date is required")
  private LocalDate birthDate;
}
