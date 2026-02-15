package com.example.cyclexbe.entity;

import com.example.cyclexbe.domain.enums.ChatMessageType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inspection Chat Message - Tin nhắn trong chat thread
 */
@Entity
@Table(name = "inspection_chat_messages")
public class InspectionChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private InspectionChatThread chatThread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private ChatMessageType type; // TEXT, IMAGE

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl; // For IMAGE type

    @Column(name = "attachment_caption", columnDefinition = "TEXT")
    private String attachmentCaption; // Optional caption for image

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public InspectionChatMessage() {}

    public InspectionChatMessage(InspectionChatThread chatThread, User sender, ChatMessageType type, String text) {
        this.chatThread = chatThread;
        this.sender = sender;
        this.type = type;
        this.text = text;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public InspectionChatThread getChatThread() { return chatThread; }
    public void setChatThread(InspectionChatThread chatThread) { this.chatThread = chatThread; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public ChatMessageType getType() { return type; }
    public void setType(ChatMessageType type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentCaption() { return attachmentCaption; }
    public void setAttachmentCaption(String attachmentCaption) { this.attachmentCaption = attachmentCaption; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

