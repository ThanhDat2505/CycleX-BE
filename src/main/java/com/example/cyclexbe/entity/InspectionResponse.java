package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Inspection Response - Phản hồi của Seller cho inspection request
 *
 * 1-to-1 mapping với InspectionRequest
 * Chứa danh sách file đã upload (response files)
 */
@Entity
@Table(name = "inspection_responses")
public class InspectionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Integer responseId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private InspectionRequest inspectionRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // Seller who submitted this response

    @Column(name = "message", columnDefinition = "TEXT")
    private String message; // Tin nhắn từ seller khi submit response

    @Column(name = "status", length = 20, nullable = false)
    private String status = "DRAFT"; // DRAFT, SUBMITTED

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // Lúc submit (nếu status = SUBMITTED)

    @OneToMany(mappedBy = "inspectionResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectionResponseFile> responseFiles = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InspectionResponse() {}

    public InspectionResponse(InspectionRequest inspectionRequest) {
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
    public Integer getResponseId() { return responseId; }
    public void setResponseId(Integer responseId) { this.responseId = responseId; }

    public InspectionRequest getInspectionRequest() { return inspectionRequest; }
    public void setInspectionRequest(InspectionRequest inspectionRequest) { this.inspectionRequest = inspectionRequest; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public List<InspectionResponseFile> getResponseFiles() { return responseFiles; }
    public void setResponseFiles(List<InspectionResponseFile> responseFiles) { this.responseFiles = responseFiles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

