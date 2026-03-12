package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * SendChatMessageRequest - Request để gửi tin nhắn TEXT
 */
public class SendChatMessageRequest {
    @NotBlank(message = "type cannot be blank")
    public String type; // TEXT, IMAGE

    @Size(max = 5000, message = "text must not exceed 5000 characters")
    public String text; // Required if type=TEXT

    public SendChatMessageRequest() {
    }

    public SendChatMessageRequest(String type, String text) {
        this.type = type;
        this.text = text;
    }
}
