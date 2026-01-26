package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class LoginRequest {
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 20, message = "Password must be between 6 and 20 characters")
    public String password;
}
