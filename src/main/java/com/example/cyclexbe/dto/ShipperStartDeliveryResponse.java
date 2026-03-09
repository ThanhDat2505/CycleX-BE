package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for POST /api/shipper/deliveries/{deliveryId}/start response
 * Returned after shipper successfully starts a delivery
 */
public class ShipperStartDeliveryResponse {

    private Integer deliveryId;
    private String deliveryStatus;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    public ShipperStartDeliveryResponse() {}

    public ShipperStartDeliveryResponse(Integer deliveryId, String deliveryStatus,
                                        String message, LocalDateTime startedAt) {
        this.deliveryId = deliveryId;
        this.deliveryStatus = deliveryStatus;
        this.message = message;
        this.startedAt = startedAt;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}
