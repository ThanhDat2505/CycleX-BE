package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateDisputeRequest {

    @NotNull
    public Integer orderId;

    @NotNull
    public Integer buyerId;

    @NotNull
    public Integer sellerId;

    @NotBlank
    @Size(max = 200)
    public String title;

    @NotBlank
    @Size(max = 1000)
    public String content;

    @NotNull
    public Integer reasonId;

    public List<String> evidenceUrls;
}
