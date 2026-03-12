package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

/**
 * MarkChatReadRequest - Request để đánh dấu tin nhắn đã đọc
 */
public class MarkChatReadRequest {
    @NotNull(message = "lastReadMessageId is required")
    @Min(value = 1, message = "lastReadMessageId must be positive")
    public Integer lastReadMessageId;

    public MarkChatReadRequest() {
    }

    public MarkChatReadRequest(Integer lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
