package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendInspectionChatTextRequest {
    @NotNull
    public Integer requestId;

    @NotBlank
    public String message;
}
