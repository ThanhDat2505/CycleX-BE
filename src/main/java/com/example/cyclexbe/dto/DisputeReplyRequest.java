package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class DisputeReplyRequest {

    @NotBlank
    public String content;

    public List<String> evidenceUrls;
}
