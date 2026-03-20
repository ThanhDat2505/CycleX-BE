package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class DisputeReplyRequest {

    @NotBlank
    public String content;
}
