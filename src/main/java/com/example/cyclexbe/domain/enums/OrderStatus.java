package com.example.cyclexbe.domain.enums;

public enum OrderStatus {
    PENDING_DELIVERY,    // Order created, waiting for delivery pickup
    IN_DELIVERY,         // Shipper is delivering
    DELIVERED,           // Delivery successful
    COMPLETED,           // Transaction fully completed
    CANCELLED,           // Order cancelled
    DISPUTED             // Dispute raised
}
