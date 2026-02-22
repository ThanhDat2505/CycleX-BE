package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inspection Response File - File được upload khi phản hồi inspection request
 *
 * Many-to-one mapping với InspectionResponse
 * Lưu metadata của file (tên, URL, kích thước)
 */
@Entity
@Table(name = "inspection_response_files")
public class InspectionResponseFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private InspectionResponse inspectionResponse;

    @Column(name = "original_file_name", length = 255, nullable = false)
    private String originalFileName;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl; // Đường dẫn file trên server (vd: /uploads/inspection-response/xxx)

    @Column(name = "content_type", length = 100)
    private String contentType; // MIME type (image/jpeg, application/pdf, etc.)

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public InspectionResponseFile() {}

    public InspectionResponseFile(InspectionResponse inspectionResponse, String originalFileName,
                                   String fileUrl, String contentType, Long sizeBytes) {
        this.inspectionResponse = inspectionResponse;
        this.originalFileName = originalFileName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }

    // Getters & Setters
    public Integer getFileId() { return fileId; }
    public void setFileId(Integer fileId) { this.fileId = fileId; }

    public InspectionResponse getInspectionResponse() { return inspectionResponse; }
    public void setInspectionResponse(InspectionResponse inspectionResponse) { this.inspectionResponse = inspectionResponse; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}

