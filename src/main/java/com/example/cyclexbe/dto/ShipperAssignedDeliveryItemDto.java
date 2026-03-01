package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for a single assigned delivery item in the shipper assigned deliveries list
 */
public class ShipperAssignedDeliveryItemDto {

    private Integer requestId;
    private String status;
    private Integer listingId;
    private String listingTitle;
    private Integer sellerId;
    private String sellerName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public ShipperAssignedDeliveryItemDto() {}

    public ShipperAssignedDeliveryItemDto(
            Integer requestId,
            String status,
            Integer listingId,
            String listingTitle,
            Integer sellerId,
            String sellerName,
            LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.status = status;
        this.listingId = listingId;
        this.listingTitle = listingTitle;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.updatedAt = updatedAt;
    }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getListingId() { return listingId; }
    public void setListingId(Integer listingId) { this.listingId = listingId; }

    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }

    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

