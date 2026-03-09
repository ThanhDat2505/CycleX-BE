package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for POST /api/shipper/deliveries/{deliveryId}/failure-report request body
 */
public class ShipperFailureReportRequest {

    @NotBlank(message = "Reason is required and cannot be empty")
    private String reason;

    public ShipperFailureReportRequest() {}

    public ShipperFailureReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
