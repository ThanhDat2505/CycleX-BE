package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.InspectionChatThread;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * InspectionChatThreadResponse - Đối tượng trả về cho chat thread
 */
public class InspectionChatThreadResponse {
    public Integer threadId;
    public Integer requestId;
    public Integer listingId;
    public String listingTitle;
    public boolean locked; // true khi listing.status == ARCHIVED
    public String lockedReason; // "LISTING_ARCHIVED" hoặc null
    public List<ChatMessageResponse> messages;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public InspectionChatThreadResponse() {}

    public InspectionChatThreadResponse(Integer threadId, Integer requestId, Integer listingId,
                                       String listingTitle, boolean locked, String lockedReason,
                                       List<ChatMessageResponse> messages,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.threadId = threadId;
        this.requestId = requestId;
        this.listingId = listingId;
        this.listingTitle = listingTitle;
        this.locked = locked;
        this.lockedReason = lockedReason;
        this.messages = messages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InspectionChatThreadResponse from(InspectionChatThread thread, List<ChatMessageResponse> messages) {
        BikeListing listing = thread.getInspectionRequest().getListing();
        boolean isLocked = listing.getStatus() == BikeListingStatus.ARCHIVED;
        String lockReason = isLocked ? "LISTING_ARCHIVED" : null;

        return new InspectionChatThreadResponse(
            thread.getThreadId(),
            thread.getInspectionRequest().getRequestId(),
            listing.getListingId(),
            listing.getTitle(),
            isLocked,
            lockReason,
            messages,
            thread.getCreatedAt(),
            thread.getUpdatedAt()
        );
    }
}

