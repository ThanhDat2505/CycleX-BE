package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class GetReviewDetailRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    @NotNull(message = "Review ID is required")
    public Integer reviewId;

    public GetReviewDetailRequest() {}
}
