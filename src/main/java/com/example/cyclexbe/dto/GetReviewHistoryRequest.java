package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;

public class GetReviewHistoryRequest {
    public String from; // YYYY-MM-DD
    public String to; // YYYY-MM-DD

    public Integer page = 0;
    public Integer pageSize = 10;

    public GetReviewHistoryRequest() {}
}
