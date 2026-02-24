package com.example.cyclexbe.domain.enums;

/**
 * Status flow for purchase requests
 */
public enum PurchaseRequestStatus {
    PENDING_SELLER_CONFIRM,  // Chờ seller confirm
    SELLER_CONFIRMED,        // Seller đã confirm
    BUYER_CONFIRMED,         // Buyer đã confirm (review/ký contract)
    COMPLETED,               // Giao dịch hoàn tất
    CANCELLED,               // Hủy bỏ
    DISPUTED                 // Tranh chấp
}

