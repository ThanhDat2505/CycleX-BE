package com.example.cyclexbe.dto;

/**
 * Response cho seller xem kết quả duyệt listing (approve/reject).
 * Bao gồm thông tin listing và inspection report (lý do approve/reject).
 */
public class ListingResultResponse {
    public SellerListingResponse listing;
    public InspectionReportResponse inspectionReport;

    public ListingResultResponse() {}

    public ListingResultResponse(SellerListingResponse listing, InspectionReportResponse inspectionReport) {
        this.listing = listing;
        this.inspectionReport = inspectionReport;
    }
}
