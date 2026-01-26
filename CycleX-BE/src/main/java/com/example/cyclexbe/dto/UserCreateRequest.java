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

    public String fullName;
    public String phone;
    public String role;
    public String status;
    public String cccd;
    public String avatarUrl;

    public Boolean isVerify; // optional
}
