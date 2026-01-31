package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class GetSellerDashboardStatsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    public GetSellerDashboardStatsRequest() {}
}
