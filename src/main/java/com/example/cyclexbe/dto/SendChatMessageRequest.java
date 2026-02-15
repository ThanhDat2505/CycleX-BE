package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * SendChatMessageRequest - Request để gửi tin nhắn TEXT
 */
public class SendChatMessageRequest {
    @NotBlank(message = "type cannot be blank")
    public String type; // TEXT, IMAGE

    public String text; // Required if type=TEXT

    public SendChatMessageRequest() {}

    public SendChatMessageRequest(String type, String text) {
        this.type = type;
        this.text = text;
    }
}

