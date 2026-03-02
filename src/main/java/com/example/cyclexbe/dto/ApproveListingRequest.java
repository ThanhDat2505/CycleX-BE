package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class ApproveListingRequest {

    @NotBlank(message = "Reason text is required")
    public String reasonText; // Lý do approve (bắt buộc)

    public String reasonCode; // Optional: MEETS_STANDARDS, GOOD_CONDITION, OTHER

    public String note; // Optional internal note

    public ApproveListingRequest() {}
}
