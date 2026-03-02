package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inspection Report - Báo cáo kiểm tra listing từ Inspector
 * Mỗi lần approve/reject đều tạo một InspectionReport ghi lại lý do quyết định.
 */
@Entity
@Table(name = "inspection_reports")
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private BikeListing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(name = "decision", length = 20, nullable = false)
    private String decision; // APPROVED or REJECTED

    @Column(name = "reason_code", length = 50)
    private String reasonCode; // e.g. DUPLICATE, INVALID_INFO, LOW_QUALITY, INAPPROPRIATE, MEETS_STANDARDS, OTHER

    @Column(name = "reason_text", columnDefinition = "TEXT", nullable = false)
    private String reasonText; // Detailed reason for the decision

    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // Optional internal note

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public InspectionReport() {}

    public InspectionReport(BikeListing listing, User inspector, String decision, String reasonCode, String reasonText, String note) {
        this.listing = listing;
        this.inspector = inspector;
        this.decision = decision;
        this.reasonCode = reasonCode;
        this.reasonText = reasonText;
        this.note = note;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }

    public BikeListing getListing() { return listing; }
    public void setListing(BikeListing listing) { this.listing = listing; }

    public User getInspector() { return inspector; }
    public void setInspector(User inspector) { this.inspector = inspector; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getReasonText() { return reasonText; }
    public void setReasonText(String reasonText) { this.reasonText = reasonText; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
