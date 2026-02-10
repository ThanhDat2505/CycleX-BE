package com.example.cyclexbe.dto;

import java.time.LocalDateTime;

public class InspectionChatMessageResponse {
    public String type; // TEXT / IMAGE
    public Integer senderId; // TEXT mới có được (từ log)
    public String senderRole; // SELLER / INSPECTOR
    public String content; // raw line hoặc caption
    public String mediaUrl; // nếu IMAGE
    public LocalDateTime createdAt;

    public InspectionChatMessageResponse() {
    }
}
