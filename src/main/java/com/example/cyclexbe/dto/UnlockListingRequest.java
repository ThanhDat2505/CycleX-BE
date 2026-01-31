package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class UnlockListingRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    @NotNull(message = "Listing ID is required")
    public Integer listingId;

    public UnlockListingRequest() {}
}
