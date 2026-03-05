package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.BuyerCancelTransactionResponse;
import com.example.cyclexbe.dto.BuyerTransactionListItemResponse;
import com.example.cyclexbe.dto.BuyerTransactionDetailResponse;
import com.example.cyclexbe.service.BuyerTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for S-54: Buyer Transaction Detail (Buyer View)
 * Handles buyer-specific transaction operations
 */
@RestController
@RequestMapping("/api/buyer/transactions")
public class BuyerTransactionController {

    private final BuyerTransactionService buyerTransactionService;

    public BuyerTransactionController(BuyerTransactionService buyerTransactionService) {
        this.buyerTransactionService = buyerTransactionService;
    }

    /**
     * GET /api/buyer/transactions
     * Load transaction list for authenticated buyer.
     */
    @GetMapping
    public ResponseEntity<List<BuyerTransactionListItemResponse>> getBuyerTransactions() {
        Integer buyerId = extractBuyerIdFromAuth();
        return ResponseEntity.ok(buyerTransactionService.getBuyerTransactions(buyerId));
    }

    /*
     * F1: GET /api/buyer/transactions/{id}
     * Load transaction detail for buyer
     *
     * Returns: transaction detail with seller info, listing info, timeline, and available actions
     * Authorization: Actor MUST be the buyer of the transaction
     *
     * @param id The transaction/purchase request ID
     * @return Transaction detail response (200 OK)
     * @throws PurchaseRequestException with status 404 if not found, or implicit 403 if buyer mismatch
     */
    @GetMapping("/{id}")
    public ResponseEntity<BuyerTransactionDetailResponse> getTransactionDetail(
            @PathVariable Integer id) {
        Integer buyerId = extractBuyerIdFromAuth();
        BuyerTransactionDetailResponse response = buyerTransactionService.getTransactionDetail(id, buyerId);
        return ResponseEntity.ok(response);
    }

    /*
     * F2: POST /api/buyer/transactions/{id}/cancel
     * Cancel transaction
     *
     * Condition: only allowed when transaction.status == PENDING_SELLER_CONFIRM
     * Action on cancel:
     *   - transaction.status -> CANCELLED
     *   - listing.status -> APPROVED
     * Output: include redirectUrl "/buyer/transactions" for UI redirect
     *
     * @param id The transaction/purchase request ID
     * @return Cancel response (200 OK) with oldStatus, newStatus, listingNewStatus, redirectUrl
     * @throws PurchaseRequestException with status 409 if transaction status != PENDING_SELLER_CONFIRM
     * @throws PurchaseRequestException with status 404 if not found or buyer mismatch
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BuyerCancelTransactionResponse> cancelTransaction(
            @PathVariable Integer id) {
        Integer buyerId = extractBuyerIdFromAuth();
        BuyerCancelTransactionResponse response = buyerTransactionService.cancelTransaction(id, buyerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Extract buyer ID from Spring Security Authentication
     * Follows the same pattern as PurchaseRequestController
     *
     * @return Buyer ID (user ID)
     * @throws RuntimeException if authentication is not available
     */
    private Integer extractBuyerIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || "anonymousUser".equals(principal)) {
            throw new RuntimeException("User is not authenticated");
        }

        // Case 1: principal is String containing userId (temporary)
        if (principal instanceof String principalStr) {
            try {
                return Integer.parseInt(principalStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID in authentication: " + principalStr);
            }
        }

        // Case 2: TODO - custom principal/UserDetails
        // Example:
        // if (principal instanceof CustomUserPrincipal custom) {
        //     return custom.getUserId();
        // }

        throw new RuntimeException("Unsupported authentication principal type: " + principal.getClass().getName());
    }
}

