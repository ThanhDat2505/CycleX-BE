package com.example.cyclexbe.exception;

/**
 * Exception when bike listing is not found or invalid for purchase request
 */
public class InvalidListingException extends RuntimeException {
    private final String errorCode;

    public InvalidListingException(String message) {
        super(message);
        this.errorCode = "INVALID_LISTING";
    }

    public InvalidListingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

