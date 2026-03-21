package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminUpdateStatusRequest {
    @NotBlank(message = "Status is required")
    public String status; // ACTIVE, SUSPENDED
}
