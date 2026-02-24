package com.example.cyclexbe.util;

/**
 * Constants for Purchase Request feature (S-50)
 */
public class PurchaseRequestConstants {

    // Maximum days in the future for desiredTime (30 days)
    public static final int MAX_DESIRED_DAYS = 30;

    // Maximum length for buyer's note
    public static final int NOTE_MAX_LENGTH = 500;

    // Estimated fees (placeholder - can be replaced by actual fee service)
    // Currently set to 0, can be configured to percentage-based or fixed amount
    // public static final BigDecimal TRANSACTION_FEE_RATE = new BigDecimal("0.05"); // 5%
    public static final String TRANSACTION_FEE_RATE_KEY = "transaction.fee.rate";

    // Error codes for business rule violations
    public static final String LISTING_NOT_APPROVED = "LISTING_NOT_APPROVED";
    public static final String DESIRED_TIME_INVALID = "DESIRED_TIME_INVALID";
    public static final String DUPLICATE_PENDING_REQUEST = "DUPLICATE_PENDING_REQUEST";
    public static final String LISTING_NOT_FOUND = "LISTING_NOT_FOUND";
    public static final String BUYER_EQUALS_SELLER = "BUYER_EQUALS_SELLER";
    public static final String BUYER_NOT_FOUND = "BUYER_NOT_FOUND";

    private PurchaseRequestConstants() {
        // Utility class, no instantiation
    }
}

