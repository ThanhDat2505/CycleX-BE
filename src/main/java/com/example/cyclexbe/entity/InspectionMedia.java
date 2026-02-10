package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspectionmedia")
public class InspectionMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Integer mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private InspectionReport inspection;

    @Column(name = "media_type", length = 20)
    private String mediaType; // IMAGE / VIDEO / ...

    @Column(name = "category", length = 50)
    private String category;  // dùng "CHAT"

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }

    // getters/setters
    public Integer getMediaId() { return mediaId; }

    public InspectionReport getInspection() { return inspection; }
    public void setInspection(InspectionReport inspection) { this.inspection = inspection; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
