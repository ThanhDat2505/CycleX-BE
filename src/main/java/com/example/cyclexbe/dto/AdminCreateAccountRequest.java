package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminCreateAccountRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    public String password;

    @NotBlank(message = "Full name is required")
    public String fullName;

    @NotBlank(message = "Phone is required")
    public String phone;

    @NotNull(message = "Role is required")
    public Role role; // SHIPPER or INSPECTOR

    public String cccd;
    public String address;
}
