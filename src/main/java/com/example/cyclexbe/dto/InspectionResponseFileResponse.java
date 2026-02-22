package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.InspectionResponseFile;

/**
 * Response DTO cho InspectionResponseFile
 * Hiển thị file draft đã upload
 */
public class InspectionResponseFileResponse {

    private Integer fileId;
    private String fileName;     // Original file name
    private String contentType;
    private Long sizeBytes;
    private String url;          // Đường dẫn file

    public InspectionResponseFileResponse() {
    }

    public InspectionResponseFileResponse(Integer fileId, String fileName, String contentType, Long sizeBytes, String url) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.url = url;
    }

    public static InspectionResponseFileResponse from(InspectionResponseFile file) {
        if (file == null) return null;

        return new InspectionResponseFileResponse(
                file.getFileId(),
                file.getOriginalFileName(),
                file.getContentType(),
                file.getSizeBytes(),
                file.getFileUrl()
        );
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "InspectionResponseFileResponse{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sizeBytes=" + sizeBytes +
                ", url='" + url + '\'' +
                '}';
    }
}