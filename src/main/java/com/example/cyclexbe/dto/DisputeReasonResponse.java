package com.example.cyclexbe.dto;

public class DisputeReasonResponse {

    public Integer reasonId;
    public String title;
    public String description;

    public DisputeReasonResponse() {
    }

    public DisputeReasonResponse(Integer reasonId, String title, String description) {
        this.reasonId = reasonId;
        this.title = title;
        this.description = description;
    }
}
