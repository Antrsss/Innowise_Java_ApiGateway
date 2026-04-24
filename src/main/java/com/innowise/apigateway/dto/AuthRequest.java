package com.innowise.apigateway.dto;

import com.innowise.apigateway.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(

    @Email(message = "Invalid email format")
    @NotBlank(message = "Login is required")
    String login,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must consist of 8 characters at least")
    String password,

    Role role
) {}