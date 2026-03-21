package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.OrderStatus;

/**
 * DTO for S-54: Buyer Cancel Transaction Response
 * POST /api/buyer/transactions/{id}/cancel
 */
public class BuyerCancelTransactionResponse {

    private Integer orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String redirectUrl;

    public BuyerCancelTransactionResponse() {}

    public BuyerCancelTransactionResponse(
            Integer orderId,
            OrderStatus oldStatus,
            OrderStatus newStatus,
            String redirectUrl) {
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.redirectUrl = redirectUrl;
    }

    // Getters & Setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public OrderStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(OrderStatus oldStatus) { this.oldStatus = oldStatus; }

    public OrderStatus getNewStatus() { return newStatus; }
    public void setNewStatus(OrderStatus newStatus) { this.newStatus = newStatus; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}

