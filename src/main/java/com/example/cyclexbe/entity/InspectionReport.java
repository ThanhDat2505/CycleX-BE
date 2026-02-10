package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspectionreport")
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Integer inspectionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", unique = true, nullable = false)
    private BikeListing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(name = "status", length = 20)
    private String status = "pending"; // pending/completed/approved/rejected...

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reinspection_of_id")
    private InspectionReport reinspectionOf;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // getters/setters
    public Integer getInspectionId() { return inspectionId; }

    public BikeListing getListing() { return listing; }
    public void setListing(BikeListing listing) { this.listing = listing; }

    public User getInspector() { return inspector; }
    public void setInspector(User inspector) { this.inspector = inspector; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }

    public InspectionReport getReinspectionOf() { return reinspectionOf; }
    public void setReinspectionOf(InspectionReport reinspectionOf) { this.reinspectionOf = reinspectionOf; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
