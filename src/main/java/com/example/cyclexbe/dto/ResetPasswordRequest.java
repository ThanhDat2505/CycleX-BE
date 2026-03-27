package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public class ResetPasswordRequest {

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "OTP is required")
    @Length(min = 6, max = 6, message = "OTP must be 6 digits")
    public String otp;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    public String newPassword;
}
