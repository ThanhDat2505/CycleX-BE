package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class LockListingRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    @NotNull(message = "Listing ID is required")
    public Integer listingId;

    public LockListingRequest() {}
}
