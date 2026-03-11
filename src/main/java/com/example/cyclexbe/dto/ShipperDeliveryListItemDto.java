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
    private String pickupAddress;
    private String dropoffAddress;
    private String status;
    private String productName;
    private String productImage;
    private String sellerName;
    private String sellerPhone;
    private String buyerName;
    private String buyerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime assignedDate;

    public ShipperDeliveryListItemDto() {}

    public ShipperDeliveryListItemDto(
            Integer deliveryId,
            Integer orderId,
            String pickupCity,
            String deliveryCity,
            String pickupAddress,
            String dropoffAddress,
            String status,
            String productName,
            String productImage,
            String sellerName,
            String sellerPhone,
            String buyerName,
            String buyerPhone,
            LocalDateTime scheduledTime,
            LocalDateTime assignedDate) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.pickupCity = pickupCity;
        this.deliveryCity = deliveryCity;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.status = status;
        this.productName = productName;
        this.productImage = productImage;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.scheduledTime = scheduledTime;
        this.assignedDate = assignedDate;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getPickupCity() { return pickupCity; }
    public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }

    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
}

