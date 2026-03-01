package com.example.cyclexbe.exception;

/**
 * Exception for authorization/permission violations
 * Used when authenticated user doesn't have permission to access a resource
 */
public class ForbiddenException extends RuntimeException {
    private final String errorCode;

    public ForbiddenException(String message) {
        super(message);
        this.errorCode = "FORBIDDEN";
    }

    public ForbiddenException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

