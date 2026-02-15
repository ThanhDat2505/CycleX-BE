package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.InspectionChatMessage;

import java.time.LocalDateTime;

/**
 * ChatMessageResponse - DTO trả về cho message trong chat thread
 */
public class ChatMessageResponse {

    private Integer id;
    private Integer senderId;
    private String senderName;
    private Role senderRole;

    private String type; // TEXT, IMAGE
    private String text; // TEXT message
    private String attachmentUrl; // IMAGE url/path
    private String attachmentCaption; // optional caption for IMAGE

    private LocalDateTime createdAt;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(Integer id,
                               Integer senderId,
                               String senderName,
                               Role senderRole,
                               String type,
                               String text,
                               String attachmentUrl,
                               String attachmentCaption,
                               LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.type = type;
        this.text = text;
        this.attachmentUrl = attachmentUrl;
        this.attachmentCaption = attachmentCaption;
        this.createdAt = createdAt;
    }

    public static ChatMessageResponse from(InspectionChatMessage msg) {
        if (msg == null) return null;

        Integer senderId = null;
        String senderName = null;
        Role senderRole = null;

        if (msg.getSender() != null) {
            senderId = msg.getSender().getUserId();
            senderName = msg.getSender().getFullName();
            senderRole = msg.getSender().getRole();
        }

        String type = msg.getType() != null ? msg.getType().name() : null;

        return new ChatMessageResponse(
                msg.getMessageId(),
                senderId,
                senderName,
                senderRole,
                type,
                msg.getText(),
                msg.getAttachmentUrl(),
                msg.getAttachmentCaption(),
                msg.getCreatedAt()
        );
    }

    // ===== getters & setters =====

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Role getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(Role senderRole) {
        this.senderRole = senderRole;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentCaption() {
        return attachmentCaption;
    }

    public void setAttachmentCaption(String attachmentCaption) {
        this.attachmentCaption = attachmentCaption;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
