package com.example.cyclexbe.controller;

import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.SellerTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for seller transaction operations (S-52: Pending Transactions, S-53: Transaction Detail)
 * API endpoints:
 * - GET /api/v1/seller/transactions/pending - List pending transactions
 * - GET /api/v1/seller/transactions/{requestId} - Get transaction detail
 * - POST /api/v1/seller/transactions/{requestId}/confirm - Confirm transaction (optional)
 * - POST /api/v1/seller/transactions/{requestId}/reject - Reject transaction (optional)
 */
@RestController
@RequestMapping("/api/v1/seller/transactions")
public class SellerTransactionController {

    private final SellerTransactionService sellerTransactionService;

    public SellerTransactionController(SellerTransactionService sellerTransactionService) {
        this.sellerTransactionService = sellerTransactionService;
    }

    /**
     * GET /api/v1/seller/transactions/pending
     * Retrieve pending transactions for authenticated seller with pagination and filtering
     *
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 10)
     * @param sortBy Sort field (default "createdAt")
     * @param sortDir Sort direction ("asc" or "desc", default "desc")
     * @param transactionType Optional filter by transaction type (PURCHASE or DEPOSIT)
     * @param keyword Optional keyword search in buyer name or listing title
     * @param authentication Current authenticated user (injected by Spring Security)
     * @return Paginated list of pending transactions
     */
    @GetMapping("/pending")
    public ResponseEntity<SellerPendingTransactionsResponse> getPendingTransactions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "transactionType", required = false) TransactionType transactionType,
            @RequestParam(name = "keyword", required = false) String keyword,
            Authentication authentication) {

        SellerPendingTransactionsResponse response = sellerTransactionService.getPendingTransactions(
                authentication,
                page,
                size,
                sortBy,
                sortDir,
                transactionType,
                keyword
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/seller/transactions/{requestId}
     * Retrieve detailed information about a specific transaction
     * Verifies the transaction belongs to the seller's listings
     *
     * @param requestId Transaction request ID
     * @param authentication Current authenticated user (injected by Spring Security)
     * @return Transaction detail response
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<SellerTransactionDetailResponse> getTransactionDetail(
            @PathVariable Integer requestId,
            Authentication authentication) {

        SellerTransactionDetailResponse response = sellerTransactionService.getTransactionDetail(
                authentication,
                requestId
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/seller/transactions/{requestId}/confirm
     * Confirm a pending transaction (change status to SELLER_CONFIRMED)
     * Only allowed if transaction status is PENDING_SELLER_CONFIRM
     *
     * @param requestId Transaction request ID
     * @param request Confirm request with optional note
     * @param authentication Current authenticated user (injected by Spring Security)
     * @return Action response with updated transaction status
     */
    @PostMapping("/{requestId}/confirm")
    public ResponseEntity<ActionTransactionResponse> confirmTransaction(
            @PathVariable Integer requestId,
            @Valid @RequestBody(required = false) ConfirmTransactionRequest request,
            Authentication authentication) {

        ActionTransactionResponse response = sellerTransactionService.confirmTransaction(
                authentication,
                requestId,
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/seller/transactions/{requestId}/reject
     * Reject a pending transaction (change status to CANCELLED)
     * Only allowed if transaction status is PENDING_SELLER_CONFIRM
     *
     * @param requestId Transaction request ID
     * @param request Reject request with mandatory reason
     * @param authentication Current authenticated user (injected by Spring Security)
     * @return Action response with updated transaction status
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ActionTransactionResponse> rejectTransaction(
            @PathVariable Integer requestId,
            @Valid @RequestBody RejectTransactionRequest request,
            Authentication authentication) {

        ActionTransactionResponse response = sellerTransactionService.rejectTransaction(
                authentication,
                requestId,
                request
        );

        return ResponseEntity.ok(response);
    }
}

