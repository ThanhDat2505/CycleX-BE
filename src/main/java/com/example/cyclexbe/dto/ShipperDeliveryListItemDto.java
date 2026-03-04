package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for a single delivery item in shipper deliveries list (S-61 F1/F2)
 * GET /api/shipper/deliveries?status=...&page=...&size=...
 */
public class ShipperDeliveryListItemDto {

    private Integer deliveryId;
    private Integer orderId;
    private String pickupCity;
    private String deliveryCity;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    public ShipperDeliveryListItemDto() {}

    public ShipperDeliveryListItemDto(
            Integer deliveryId,
            Integer orderId,
            String pickupCity,
            String deliveryCity,
            String status,
            LocalDateTime scheduledTime) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.pickupCity = pickupCity;
        this.deliveryCity = deliveryCity;
        this.status = status;
        this.scheduledTime = scheduledTime;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getPickupCity() { return pickupCity; }
    public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }

    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
}

