package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inspection Requirement - Yêu cầu bổ sung từ Inspector
 *
 * Mỗi InspectionRequest có thể có nhiều requirements
 * Seller phải phản hồi các requirements chưa resolved
 */
@Entity
@Table(name = "inspection_requirements")
public class InspectionRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirement_id")
    private Integer requirementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private InspectionRequest inspectionRequest;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung yêu cầu (vd: "Ảnh số khung xe chưa rõ")

    @Column(name = "required_text", nullable = false)
    private boolean requiredText = false; // Yêu cầu text response

    @Column(name = "required_files", nullable = false)
    private boolean requiredFiles = false; // Yêu cầu file response

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false; // Đã được seller phản hồi chưa?

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InspectionRequirement() {}

    public InspectionRequirement(InspectionRequest inspectionRequest, String content,
                                  boolean requiredText, boolean requiredFiles) {
        this.inspectionRequest = inspectionRequest;
        this.content = content;
        this.requiredText = requiredText;
        this.requiredFiles = requiredFiles;
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
    public Integer getRequirementId() { return requirementId; }
    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

    public InspectionRequest getInspectionRequest() { return inspectionRequest; }
    public void setInspectionRequest(InspectionRequest inspectionRequest) { this.inspectionRequest = inspectionRequest; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRequiredText() { return requiredText; }
    public void setRequiredText(boolean requiredText) { this.requiredText = requiredText; }

    public boolean isRequiredFiles() { return requiredFiles; }
    public void setRequiredFiles(boolean requiredFiles) { this.requiredFiles = requiredFiles; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

