package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminOverrideRequest {

    @NotBlank
    public String action; // BUYER_WIN, SELLER_WIN, SPLIT

    @NotBlank
    public String reason;
}
