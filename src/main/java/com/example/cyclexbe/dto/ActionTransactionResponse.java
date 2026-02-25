package com.example.cyclexbe.dto;

import java.time.LocalDateTime;

/**
 * DTO for response after confirming/rejecting a transaction
 */
public class ActionTransactionResponse {

    private Integer requestId;
    private String status;
    private String displayStatus;
    private String message;
    private LocalDateTime updatedAt;

    // No-args constructor
    public ActionTransactionResponse() {
    }

    // All-args constructor
    public ActionTransactionResponse(
            Integer requestId,
            String status,
            String displayStatus,
            String message,
            LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.status = status;
        this.displayStatus = displayStatus;
        this.message = message;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Integer getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

