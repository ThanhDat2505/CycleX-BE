package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class VerifyOtpRequest {
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "OTP is required")
    @Length(min = 6, max = 6, message = "OTP must be 6 digits")
    public String otp;
}
