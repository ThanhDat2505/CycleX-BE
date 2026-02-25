package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for seller transaction operations (S-52 and S-53)
 */
@Service
public class SellerTransactionServiceImpl implements SellerTransactionService {

    private final PurchaseRequestRepository purchaseRequestRepository;

    public SellerTransactionServiceImpl(PurchaseRequestRepository purchaseRequestRepository) {
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    @Override
    public SellerPendingTransactionsResponse getPendingTransactions(
            Authentication authentication,
            int page,
            int size,
            String sortBy,
            String sortDir,
            TransactionType transactionType,
            String keyword) {

        // Extract current seller ID
        Integer sellerId = parseCurrentUserId(authentication);

        // Validate sortBy
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }
        if (!sortBy.equals("createdAt") && !sortBy.equals("requestId")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy parameter. Allowed values: createdAt, requestId"
            );
        }

        // Validate sortDir
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "desc";
        }
        if (!sortDir.toLowerCase().equals("asc") && !sortDir.toLowerCase().equals("desc")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortDir parameter. Allowed values: asc, desc"
            );
        }

        // Create Pageable
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Query from repository
        Page<PurchaseRequest> pageResult = purchaseRequestRepository.findPendingTransactionsForSeller(
                sellerId,
                transactionType,
                keyword,
                pageable
        );

        // Map to DTOs
        List<PendingTransactionListItemResponse> items = pageResult.getContent()
                .stream()
                .map(this::mapToPendingTransactionListItem)
                .collect(Collectors.toList());

        // Build response
        SellerPendingTransactionsResponse response = new SellerPendingTransactionsResponse();
        response.setContent(items);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setSortBy(sortBy);
        response.setSortDir(sortDir);

        // Set applied filters
        SellerPendingTransactionsResponse.AppliedFilters filters =
                new SellerPendingTransactionsResponse.AppliedFilters(
                        "PENDING_SELLER_CONFIRM",
                        transactionType != null ? transactionType.toString() : null
                );
        response.setAppliedFilters(filters);

        return response;
    }

    @Override
    public SellerTransactionDetailResponse getTransactionDetail(
            Authentication authentication,
            Integer requestId) {

        Integer sellerId = parseCurrentUserId(authentication);

        // Find transaction and verify it belongs to seller
        Optional<PurchaseRequest> optionalTransaction = purchaseRequestRepository.findByIdAndSeller(requestId, sellerId);

        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transaction not found or you don't have permission to access it"
            );
        }

        PurchaseRequest transaction = optionalTransaction.get();
        return mapToTransactionDetail(transaction);
    }

    @Override
    public ActionTransactionResponse confirmTransaction(
            Authentication authentication,
            Integer requestId,
            ConfirmTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        // Find transaction and verify seller
        Optional<PurchaseRequest> optionalTransaction = purchaseRequestRepository.findByIdAndSeller(requestId, sellerId);
        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transaction not found or you don't have permission"
            );
        }

        PurchaseRequest transaction = optionalTransaction.get();

        // Check status
        if (!transaction.getStatus().equals(PurchaseRequestStatus.PENDING_SELLER_CONFIRM)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction status does not allow confirmation. Current status: " + transaction.getStatus()
            );
        }

        // Update status
        transaction.setStatus(PurchaseRequestStatus.SELLER_CONFIRMED);
        if (request != null && request.getNote() != null) {
            transaction.setNote(request.getNote());
        }

        PurchaseRequest updated = purchaseRequestRepository.save(transaction);

        return mapToActionResponse(updated, "Transaction confirmed successfully");
    }

    @Override
    public ActionTransactionResponse rejectTransaction(
            Authentication authentication,
            Integer requestId,
            RejectTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        // Validate request
        if (request == null || request.getReason() == null || request.getReason().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reason is required"
            );
        }

        // Find transaction and verify seller
        Optional<PurchaseRequest> optionalTransaction = purchaseRequestRepository.findByIdAndSeller(requestId, sellerId);
        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transaction not found or you don't have permission"
            );
        }

        PurchaseRequest transaction = optionalTransaction.get();

        // Check status
        if (!transaction.getStatus().equals(PurchaseRequestStatus.PENDING_SELLER_CONFIRM)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction status does not allow rejection. Current status: " + transaction.getStatus()
            );
        }

        // Update status
        transaction.setStatus(PurchaseRequestStatus.CANCELLED);
        transaction.setNote("REJECTED by seller: " + request.getReason());

        PurchaseRequest updated = purchaseRequestRepository.save(transaction);

        return mapToActionResponse(updated, "Transaction rejected successfully");
    }

    /**
     * Extract the seller ID from the authentication principal
     * The subject (principal) is stored as userId string in JWT
     */
    private Integer parseCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No authentication found"
            );
        }

        String principal = authentication.getPrincipal().toString();
        try {
            return Integer.parseInt(principal);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid user ID in token: " + principal
            );
        }
    }

    /**
     * Map PurchaseRequest entity to PendingTransactionListItemResponse DTO
     */
    private PendingTransactionListItemResponse mapToPendingTransactionListItem(PurchaseRequest pr) {
        PendingTransactionListItemResponse response = new PendingTransactionListItemResponse();
        response.setRequestId(pr.getRequestId());
        response.setBuyerName(pr.getBuyer().getFullName());
        response.setListingTitle(pr.getListing().getTitle());
        response.setTransactionType(pr.getTransactionType().toString());
        response.setCreatedAt(pr.getCreatedAt());
        response.setStatus(pr.getStatus().toString());
        response.setDisplayStatus(getDisplayStatus(pr.getStatus()));
        return response;
    }

    /**
     * Map PurchaseRequest entity to SellerTransactionDetailResponse DTO
     */
    private SellerTransactionDetailResponse mapToTransactionDetail(PurchaseRequest pr) {
        SellerTransactionDetailResponse response = new SellerTransactionDetailResponse();
        response.setRequestId(pr.getRequestId());
        response.setBuyerName(pr.getBuyer().getFullName());
        response.setBuyerEmail(pr.getBuyer().getEmail());
        response.setBuyerPhone(pr.getBuyer().getPhone());
        response.setListingTitle(pr.getListing().getTitle());
        response.setListingId(pr.getListing().getListingId());
        response.setTransactionType(pr.getTransactionType().toString());
        response.setDepositAmount(pr.getDepositAmount());
        response.setPlatformFee(pr.getPlatformFee());
        response.setInspectionFee(pr.getInspectionFee());
        response.setStatus(pr.getStatus().toString());
        response.setDisplayStatus(getDisplayStatus(pr.getStatus()));
        response.setNote(pr.getNote());
        response.setDesiredTransactionTime(pr.getDesiredTransactionTime());
        response.setCreatedAt(pr.getCreatedAt());
        response.setUpdatedAt(pr.getUpdatedAt());
        return response;
    }

    /**
     * Map PurchaseRequest entity to ActionTransactionResponse DTO
     */
    private ActionTransactionResponse mapToActionResponse(PurchaseRequest pr, String message) {
        ActionTransactionResponse response = new ActionTransactionResponse();
        response.setRequestId(pr.getRequestId());
        response.setStatus(pr.getStatus().toString());
        response.setDisplayStatus(getDisplayStatus(pr.getStatus()));
        response.setMessage(message);
        response.setUpdatedAt(pr.getUpdatedAt());
        return response;
    }

    /**
     * Get Vietnamese display status for the UI
     */
    private String getDisplayStatus(PurchaseRequestStatus status) {
        return switch (status) {
            case PENDING_SELLER_CONFIRM -> "Chờ xác nhận";
            case SELLER_CONFIRMED -> "Seller đã xác nhận";
            case BUYER_CONFIRMED -> "Buyer đã xác nhận";
            case COMPLETED -> "Hoàn tất";
            case CANCELLED -> "Hủy bỏ";
            case DISPUTED -> "Tranh chấp";
            default -> status.toString();
        };
    }
}

