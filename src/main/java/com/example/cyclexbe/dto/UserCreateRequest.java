package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    public String email;

    // raw password từ client, sẽ hash trong service
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password is invalid")
    public String password;

    @NotBlank(message = "Phone is required")
    public String phone;

    @NotBlank(message = "CCCD is required")
    public String cccd;

    public String fullName;

    public String role;
    public String status;

    public String avatarUrl;

    public Boolean isVerify; // optional
}
