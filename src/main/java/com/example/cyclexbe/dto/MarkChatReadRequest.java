package com.example.cyclexbe.dto;

/**
 * MarkChatReadRequest - Request để đánh dấu tin nhắn đã đọc
 */
public class MarkChatReadRequest {
    public Integer lastReadMessageId;

    public MarkChatReadRequest() {}

    public MarkChatReadRequest(Integer lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}

