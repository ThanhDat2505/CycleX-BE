package com.example.cyclexbe.exception;

/**
 * Exception for purchase request business logic violations
 */
public class PurchaseRequestException extends RuntimeException {
    private final String errorCode;

    public PurchaseRequestException(String message) {
        super(message);
        this.errorCode = "PURCHASE_REQUEST_ERROR";
    }

    public PurchaseRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

