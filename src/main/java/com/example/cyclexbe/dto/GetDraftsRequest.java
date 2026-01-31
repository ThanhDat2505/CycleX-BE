package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class GetDraftsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    public String sort = "newest";

    @Min(value = 0, message = "Page must be >= 0")
    public Integer page = 0;

    @Min(value = 1, message = "Page size must be >= 1")
    public Integer pageSize = 10;

    public GetDraftsRequest() {}
}
