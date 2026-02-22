package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO để submit inspection response
 */
public class SubmitInspectionResponseRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message; // Tin nhắn từ seller

    public SubmitInspectionResponseRequest() {
    }

    public SubmitInspectionResponseRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SubmitInspectionResponseRequest{" +
                "message='" + message + '\'' +
                '}';
    }
}