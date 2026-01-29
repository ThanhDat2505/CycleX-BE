package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendOtpRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    public String email;
}
