package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for rejecting a transaction (S-52 optional feature)
 */
public class RejectTransactionRequest {

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    // No-args constructor
    public RejectTransactionRequest() {
    }

    // All-args constructor
    public RejectTransactionRequest(String reason) {
        this.reason = reason;
    }

    // Getters
    public String getReason() {
        return reason;
    }

    // Setters
    public void setReason(String reason) {
        this.reason = reason;
    }
}

