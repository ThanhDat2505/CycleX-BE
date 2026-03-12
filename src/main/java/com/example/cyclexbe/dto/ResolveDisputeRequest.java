package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResolveDisputeRequest {

    @NotBlank
    public String action; // REFUND_BUYER, RELEASE_FUND_SELLER, CLOSE_CASE

    @NotBlank
    public String resolutionNote;
}
