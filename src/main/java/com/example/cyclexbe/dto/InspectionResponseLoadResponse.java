package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.InspectionRequest;
import com.example.cyclexbe.entity.InspectionResponse;

import java.util.List;

/**
 * Main Response DTO cho GET /api/v1/seller/listings/{listingId}/inspection-response
 * Tổng hợp tất cả dữ liệu cần thiết để hiển thị S-42 screen
 */
public class InspectionResponseLoadResponse {

    private boolean featureEnabled; // Feature flag
    private Integer listingId;
    private Integer inspectionRequestId;
    private String listingStatus; // WAITING_SELLER_RESPONSE, WAITING_INSPECTOR_REVIEW, etc.
    private List<InspectionRequirementResponse> inspectorRequirements; // Danh sách requirements chưa resolved
    private List<InspectionResponseFileResponse> draftFiles; // Danh sách file draft
    private UploadPolicyResponse uploadPolicy; // Chính sách upload
    private LocksResponse locks; // Trạng thái lock

    public InspectionResponseLoadResponse() {
    }

    public InspectionResponseLoadResponse(boolean featureEnabled,
                                          Integer listingId,
                                          Integer inspectionRequestId,
                                          String listingStatus,
                                          List<InspectionRequirementResponse> inspectorRequirements,
                                          List<InspectionResponseFileResponse> draftFiles,
                                          UploadPolicyResponse uploadPolicy,
                                          LocksResponse locks) {
        this.featureEnabled = featureEnabled;
        this.listingId = listingId;
        this.inspectionRequestId = inspectionRequestId;
        this.listingStatus = listingStatus;
        this.inspectorRequirements = inspectorRequirements;
        this.draftFiles = draftFiles;
        this.uploadPolicy = uploadPolicy;
        this.locks = locks;
    }

    public static InspectionResponseLoadResponse from(BikeListing listing,
                                                      InspectionRequest request,
                                                      InspectionResponse response,
                                                      List<InspectionRequirementResponse> requirements,
                                                      List<InspectionResponseFileResponse> files) {

        UploadPolicyResponse policy = new UploadPolicyResponse(
                10,
                10,
                List.of("image/jpeg", "image/png", "application/pdf")
        );

        return new InspectionResponseLoadResponse(
                true,
                listing.getListingId(),
                request.getRequestId(),
                listing.getStatus().toString(),
                requirements,
                files,
                policy,
                LocksResponse.from(response)
        );
    }

    public boolean isFeatureEnabled() {
        return featureEnabled;
    }

    public void setFeatureEnabled(boolean featureEnabled) {
        this.featureEnabled = featureEnabled;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public Integer getInspectionRequestId() {
        return inspectionRequestId;
    }

    public void setInspectionRequestId(Integer inspectionRequestId) {
        this.inspectionRequestId = inspectionRequestId;
    }

    public String getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(String listingStatus) {
        this.listingStatus = listingStatus;
    }

    public List<InspectionRequirementResponse> getInspectorRequirements() {
        return inspectorRequirements;
    }

    public void setInspectorRequirements(List<InspectionRequirementResponse> inspectorRequirements) {
        this.inspectorRequirements = inspectorRequirements;
    }

    public List<InspectionResponseFileResponse> getDraftFiles() {
        return draftFiles;
    }

    public void setDraftFiles(List<InspectionResponseFileResponse> draftFiles) {
        this.draftFiles = draftFiles;
    }

    public UploadPolicyResponse getUploadPolicy() {
        return uploadPolicy;
    }

    public void setUploadPolicy(UploadPolicyResponse uploadPolicy) {
        this.uploadPolicy = uploadPolicy;
    }

    public LocksResponse getLocks() {
        return locks;
    }

    public void setLocks(LocksResponse locks) {
        this.locks = locks;
    }

    @Override
    public String toString() {
        return "InspectionResponseLoadResponse{" +
                "featureEnabled=" + featureEnabled +
                ", listingId=" + listingId +
                ", inspectionRequestId=" + inspectionRequestId +
                ", listingStatus='" + listingStatus + '\'' +
                ", inspectorRequirements=" + inspectorRequirements +
                ", draftFiles=" + draftFiles +
                ", uploadPolicy=" + uploadPolicy +
                ", locks=" + locks +
                '}';
    }
}