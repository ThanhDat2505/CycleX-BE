package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class GetInspectorListingsRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    public String status; // ALL, PENDING, REVIEWING
    public String sort = "newest"; // newest, oldest

    @Min(value = 0, message = "Page must be >= 0")
    public Integer page = 0;

    @Min(value = 1, message = "Page size must be >= 1")
    public Integer pageSize = 10;

    public GetInspectorListingsRequest() {}
}
