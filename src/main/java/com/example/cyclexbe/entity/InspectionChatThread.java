package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inspection Chat Thread - Thread chat giữa inspector và seller
 * Mapping: 1-1 với InspectionRequest
 */
@Entity
@Table(name = "inspection_chat_threads")
public class InspectionChatThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    private Integer threadId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private InspectionRequest inspectionRequest;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InspectionChatThread() {}

    public InspectionChatThread(InspectionRequest inspectionRequest) {
        this.inspectionRequest = inspectionRequest;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getThreadId() { return threadId; }
    public void setThreadId(Integer threadId) { this.threadId = threadId; }

    public InspectionRequest getInspectionRequest() { return inspectionRequest; }
    public void setInspectionRequest(InspectionRequest inspectionRequest) { this.inspectionRequest = inspectionRequest; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

