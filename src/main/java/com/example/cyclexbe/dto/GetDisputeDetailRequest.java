package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class GetDisputeDetailRequest {
    @NotNull(message = "Inspector ID is required")
    public Integer inspectorId;

    @NotNull(message = "Dispute ID is required")
    public Integer disputeId;

    public GetDisputeDetailRequest() {}
}
