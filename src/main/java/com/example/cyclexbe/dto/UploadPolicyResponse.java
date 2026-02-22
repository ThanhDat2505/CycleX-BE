package com.example.cyclexbe.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Upload Policy DTO - Chính sách upload file
 * Được trả về trong screen load response
 */
public class UploadPolicyResponse {
    private Integer maxFiles;      // Max 10 files
    private Integer maxFileSizeMb; // Max 10 MB per file
    private List<String> allowedTypes = new ArrayList<>(); // ["image/jpeg", "image/png", "application/pdf"]

    public UploadPolicyResponse() {
    }

    public UploadPolicyResponse(Integer maxFiles, Integer maxFileSizeMb, List<String> allowedTypes) {
        this.maxFiles = maxFiles;
        this.maxFileSizeMb = maxFileSizeMb;
        setAllowedTypes(allowedTypes); // tránh null
    }

    public Integer getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(Integer maxFiles) {
        this.maxFiles = maxFiles;
    }

    public Integer getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(Integer maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public List<String> getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = (allowedTypes == null) ? new ArrayList<>() : new ArrayList<>(allowedTypes);
    }

    @Override
    public String toString() {
        return "UploadPolicyResponse{" +
                "maxFiles=" + maxFiles +
                ", maxFileSizeMb=" + maxFileSizeMb +
                ", allowedTypes=" + allowedTypes +
                '}';
    }
}