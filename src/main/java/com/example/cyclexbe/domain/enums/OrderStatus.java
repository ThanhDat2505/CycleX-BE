package com.example.cyclexbe.domain.enums;

public enum OrderStatus {
    PENDING_SELLER_CONFIRM, // Buyer placed order, waiting for seller to confirm
    PENDING_DELIVERY, // Seller confirmed, waiting for delivery pickup
    IN_DELIVERY, // Shipper is delivering
    DELIVERED, // Delivery successful
    COMPLETED, // Transaction fully completed
    CANCELLED, // Order cancelled
    DISPUTED // Dispute raised
}
