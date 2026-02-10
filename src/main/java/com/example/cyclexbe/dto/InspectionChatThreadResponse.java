package com.example.cyclexbe.dto;

import java.util.List;

public class InspectionChatThreadResponse {
    public Integer requestId;
    public Integer listingId;
    public boolean archived;
    public List<InspectionChatMessageResponse> messages;
}
