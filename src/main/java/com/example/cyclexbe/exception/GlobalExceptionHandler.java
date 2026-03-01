package com.example.cyclexbe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(Map.of(
                "message", "Validation failed",
                "errors", errors
        ));
    }

    // ✅ THÊM ĐOẠN NÀY
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
        assert ex.getReason() != null;
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status", ex.getStatusCode().value(),
                        "message", ex.getReason()
                ));
    }

    /**
     * Handle PurchaseRequestException - business rule violations in S-50 and S-54
     * Maps error codes to appropriate HTTP status codes:
     * - TRANSACTION_NOT_FOUND -> 404
     * - INVALID_TRANSACTION_STATUS -> 409
     * - Other -> 400
     */
    @ExceptionHandler(PurchaseRequestException.class)
    public ResponseEntity<?> handlePurchaseRequestException(PurchaseRequestException ex) {
        int status = switch (ex.getErrorCode()) {
            case "TRANSACTION_NOT_FOUND" -> HttpStatus.NOT_FOUND.value();
            case "INVALID_TRANSACTION_STATUS" -> HttpStatus.CONFLICT.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };

        Map<String, Object> response = Map.of(
                "status", status,
                "errorCode", ex.getErrorCode(),
                "message", ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handle InvalidListingException - listing validation errors in S-50
     */
    @ExceptionHandler(InvalidListingException.class)
    public ResponseEntity<?> handleInvalidListingException(InvalidListingException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "errorCode", ex.getErrorCode(),
                "message", ex.getMessage()
        ));
    }

    /**
     * Handle ForbiddenException - authorization errors (403)
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "errorCode", ex.getErrorCode(),
                "message", ex.getMessage()
        ));
    }
}
