package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.InspectionRequirement;

/**
 * Response DTO cho InspectionRequirement
 * Hiển thị requirement cần phản hồi
 */
public class InspectionRequirementResponse {
    private Integer requirementId;
    private String content;
    private boolean requiredText;
    private boolean requiredFiles;
    private boolean resolved;

    public InspectionRequirementResponse() {}

    public InspectionRequirementResponse(Integer requirementId, String content, boolean requiredText,
                                        boolean requiredFiles, boolean resolved) {
        this.requirementId = requirementId;
        this.content = content;
        this.requiredText = requiredText;
        this.requiredFiles = requiredFiles;
        this.resolved = resolved;
    }

    public static InspectionRequirementResponse from(InspectionRequirement requirement) {
        return new InspectionRequirementResponse(
                requirement.getRequirementId(),
                requirement.getContent(),
                requirement.isRequiredText(),
                requirement.isRequiredFiles(),
                requirement.isResolved()
        );
    }

    // Getters & Setters
    public Integer getRequirementId() { return requirementId; }
    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRequiredText() { return requiredText; }
    public void setRequiredText(boolean requiredText) { this.requiredText = requiredText; }

    public boolean isRequiredFiles() { return requiredFiles; }
    public void setRequiredFiles(boolean requiredFiles) { this.requiredFiles = requiredFiles; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}


