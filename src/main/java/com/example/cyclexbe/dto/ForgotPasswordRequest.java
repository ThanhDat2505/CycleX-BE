package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    public String email;
}
