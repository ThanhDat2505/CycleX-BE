package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.InspectionReport;

import java.time.LocalDateTime;

public class InspectionReportResponse {
    public Integer reportId;
    public Integer listingId;
    public String listingTitle;
    public Integer inspectorId;
    public String inspectorName;
    public String decision;
    public String reasonCode;
    public String reasonText;
    public String note;
    public LocalDateTime createdAt;

    public InspectionReportResponse() {}

    public static InspectionReportResponse from(InspectionReport r) {
        if (r == null) return null;
        InspectionReportResponse dto = new InspectionReportResponse();
        dto.reportId = r.getReportId();
        dto.listingId = r.getListing() != null ? r.getListing().getListingId() : null;
        dto.listingTitle = r.getListing() != null ? r.getListing().getTitle() : null;
        dto.inspectorId = r.getInspector() != null ? r.getInspector().getUserId() : null;
        dto.inspectorName = r.getInspector() != null ? r.getInspector().getFullName() : null;
        dto.decision = r.getDecision();
        dto.reasonCode = r.getReasonCode();
        dto.reasonText = r.getReasonText();
        dto.note = r.getNote();
        dto.createdAt = r.getCreatedAt();
        return dto;
    }
}
