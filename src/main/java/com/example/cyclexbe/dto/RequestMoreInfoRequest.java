package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class RequestMoreInfoRequest {

    @NotBlank
    public String message;
}
