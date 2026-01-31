package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class RejectListingRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    @NotNull(message = "Listing ID is required")
    public Integer listingId;

    @NotBlank(message = "Reason code is required")
    public String reasonCode; // DUPLICATE, INVALID_INFO, LOW_QUALITY, INAPPROPRIATE, OTHER

    @NotBlank(message = "Reason text is required")
    public String reasonText;

    public String note; // optional

    public RejectListingRequest() {}
}
