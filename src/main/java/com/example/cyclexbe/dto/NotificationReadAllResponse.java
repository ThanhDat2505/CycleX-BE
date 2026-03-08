package com.example.cyclexbe.dto;

public class NotificationReadAllResponse {

    private long updatedCount;
    private String message;

    public NotificationReadAllResponse() {}

    public NotificationReadAllResponse(long updatedCount, String message) {
        this.updatedCount = updatedCount;
        this.message = message;
    }

    public long getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(long updatedCount) { this.updatedCount = updatedCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
