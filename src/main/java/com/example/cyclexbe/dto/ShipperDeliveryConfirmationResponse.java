package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for GET /api/shipper/deliveries/{deliveryId}/confirmation
 * Load delivery info for confirmation screen (S-63)
 */
public class ShipperDeliveryConfirmationResponse {

    private Integer deliveryId;
    private Integer orderId;
    private String status;

    private String pickupAddress;
    private String dropoffAddress;

    private String buyerName;
    private String buyerPhone;

    private String listingTitle;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime assignedTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime shippedTime;

    private boolean canConfirm;

    public ShipperDeliveryConfirmationResponse() {}

    public ShipperDeliveryConfirmationResponse(
            Integer deliveryId, Integer orderId, String status,
            String pickupAddress, String dropoffAddress,
            String buyerName, String buyerPhone,
            String listingTitle,
            LocalDateTime assignedTime, LocalDateTime shippedTime,
            boolean canConfirm) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.listingTitle = listingTitle;
        this.assignedTime = assignedTime;
        this.shippedTime = shippedTime;
        this.canConfirm = canConfirm;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }

    public LocalDateTime getAssignedTime() { return assignedTime; }
    public void setAssignedTime(LocalDateTime assignedTime) { this.assignedTime = assignedTime; }

    public LocalDateTime getShippedTime() { return shippedTime; }
    public void setShippedTime(LocalDateTime shippedTime) { this.shippedTime = shippedTime; }

    public boolean isCanConfirm() { return canConfirm; }
    public void setCanConfirm(boolean canConfirm) { this.canConfirm = canConfirm; }
}
