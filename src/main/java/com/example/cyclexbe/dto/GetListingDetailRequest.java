package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class GetListingDetailRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    @NotNull(message = "Listing ID is required")
    public Integer listingId;

    public GetListingDetailRequest() {}
}
