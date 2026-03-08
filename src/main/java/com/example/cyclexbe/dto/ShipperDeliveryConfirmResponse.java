package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for POST /api/shipper/deliveries/{deliveryId}/confirm response (S-63)
 * Returned after successful delivery confirmation
 */
public class ShipperDeliveryConfirmResponse {

    private Integer deliveryId;
    private String deliveryStatus;
    private String transactionStatus;
    private String listingStatus;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime confirmedAt;

    public ShipperDeliveryConfirmResponse() {}

    public ShipperDeliveryConfirmResponse(
            Integer deliveryId, String deliveryStatus,
            String transactionStatus, String listingStatus,
            String message, LocalDateTime confirmedAt) {
        this.deliveryId = deliveryId;
        this.deliveryStatus = deliveryStatus;
        this.transactionStatus = transactionStatus;
        this.listingStatus = listingStatus;
        this.message = message;
        this.confirmedAt = confirmedAt;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getListingStatus() { return listingStatus; }
    public void setListingStatus(String listingStatus) { this.listingStatus = listingStatus; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
}
