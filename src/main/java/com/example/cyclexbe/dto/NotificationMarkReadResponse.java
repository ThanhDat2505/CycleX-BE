package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationMarkReadResponse {

    private Integer notificationId;

    @JsonProperty("isRead")
    private boolean isRead;

    private String message;

    public NotificationMarkReadResponse() {}

    public NotificationMarkReadResponse(Integer notificationId, boolean isRead, String message) {
        this.notificationId = notificationId;
        this.isRead = isRead;
        this.message = message;
    }

    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
