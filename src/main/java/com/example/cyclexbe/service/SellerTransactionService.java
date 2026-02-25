package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.*;
import org.springframework.security.core.Authentication;

/**
 * Service interface for seller transaction operations (S-52 and S-53)
 */
public interface SellerTransactionService {

    /**
     * Get pending transactions for the authenticated seller
     * with pagination, sorting, and optional filtering
     */
    SellerPendingTransactionsResponse getPendingTransactions(
            Authentication authentication,
            int page,
            int size,
            String sortBy,
            String sortDir,
            TransactionType transactionType,
            String keyword
    );

    /**
     * Get detailed view of a specific transaction for the seller
     * Verifies the transaction belongs to the seller's listings
     */
    SellerTransactionDetailResponse getTransactionDetail(
            Authentication authentication,
            Integer requestId
    );

    /**
     * Confirm a pending transaction
     * Only allowed if status is PENDING_SELLER_CONFIRM
     */
    ActionTransactionResponse confirmTransaction(
            Authentication authentication,
            Integer requestId,
            ConfirmTransactionRequest request
    );

    /**
     * Reject a pending transaction
     * Only allowed if status is PENDING_SELLER_CONFIRM
     */
    ActionTransactionResponse rejectTransaction(
            Authentication authentication,
            Integer requestId,
            RejectTransactionRequest request
    );
}

