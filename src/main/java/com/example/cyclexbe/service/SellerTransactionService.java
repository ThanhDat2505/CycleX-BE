package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerTransactionService {

    private final PurchaseRequestRepository purchaseRequestRepository;

    public SellerTransactionService(PurchaseRequestRepository purchaseRequestRepository) {
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    // ==============================
    // S-52: Get Pending Transactions
    // ==============================
    public SellerPendingTransactionsResponse getPendingTransactions(
            Authentication authentication,
            int page,
            int size,
            String sortBy,
            String sortDir,
            TransactionType transactionType,
            String keyword) {

        Integer sellerId = parseCurrentUserId(authentication);

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }

        if (!sortBy.equals("createdAt") && !sortBy.equals("requestId")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy parameter. Allowed values: createdAt, requestId"
            );
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "desc";
        }

        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortDir parameter. Allowed values: asc, desc"
            );
        }

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PurchaseRequest> pageResult =
                purchaseRequestRepository.findPendingTransactionsForSeller(
                        sellerId,
                        transactionType,
                        keyword,
                        pageable
                );

        List<PendingTransactionListItemResponse> items =
                pageResult.getContent()
                        .stream()
                        .map(this::mapToPendingTransactionListItem)
                        .collect(Collectors.toList());

        SellerPendingTransactionsResponse response =
                new SellerPendingTransactionsResponse();

        response.setContent(items);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setSortBy(sortBy);
        response.setSortDir(sortDir);

        SellerPendingTransactionsResponse.AppliedFilters filters =
                new SellerPendingTransactionsResponse.AppliedFilters(
                        "PENDING_SELLER_CONFIRM",
                        transactionType != null ? transactionType.toString() : null
                );

        response.setAppliedFilters(filters);

        return response;
    }

    // ==============================
    // S-53: Transaction Detail
    // ==============================
    public SellerTransactionDetailResponse getTransactionDetail(
            Authentication authentication,
            Integer requestId) {

        Integer sellerId = parseCurrentUserId(authentication);

        Optional<PurchaseRequest> optionalTransaction =
                purchaseRequestRepository
                        .findByRequestIdAndListing_Seller_UserId(requestId, sellerId);

        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transaction not found or you don't have permission"
            );
        }

        return mapToTransactionDetail(optionalTransaction.get());
    }

    // ==============================
    // Confirm Transaction
    // ==============================
    public ActionTransactionResponse confirmTransaction(
            Authentication authentication,
            Integer requestId,
            ConfirmTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        PurchaseRequest transaction =
                purchaseRequestRepository
                        .findByRequestIdAndListing_Seller_UserId(requestId, sellerId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found or you don't have permission"
                                )
                        );

        if (!transaction.getStatus()
                .equals(PurchaseRequestStatus.PENDING_SELLER_CONFIRM)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction cannot be confirmed in current status"
            );
        }

        transaction.setStatus(PurchaseRequestStatus.SELLER_CONFIRMED);

        if (request != null && request.getNote() != null) {
            transaction.setNote(request.getNote());
        }

        PurchaseRequest updated =
                purchaseRequestRepository.save(transaction);

        return mapToActionResponse(updated, "Transaction confirmed successfully");
    }

    // ==============================
    // Reject Transaction
    // ==============================
    public ActionTransactionResponse rejectTransaction(
            Authentication authentication,
            Integer requestId,
            RejectTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        if (request == null || request.getReason() == null
                || request.getReason().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reason is required"
            );
        }

        PurchaseRequest transaction =
                purchaseRequestRepository
                        .findByRequestIdAndListing_Seller_UserId(requestId, sellerId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found or you don't have permission"
                                )
                        );

        if (!transaction.getStatus()
                .equals(PurchaseRequestStatus.PENDING_SELLER_CONFIRM)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction cannot be rejected in current status"
            );
        }

        transaction.setStatus(PurchaseRequestStatus.CANCELLED);
        transaction.setNote("REJECTED by seller: " + request.getReason());

        PurchaseRequest updated =
                purchaseRequestRepository.save(transaction);

        return mapToActionResponse(updated, "Transaction rejected successfully");
    }

    // ==============================
    // Helper Methods
    // ==============================

    private Integer parseCurrentUserId(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No authentication found"
            );
        }

        try {
            return Integer.parseInt(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid user ID in token"
            );
        }
    }

    private PendingTransactionListItemResponse
    mapToPendingTransactionListItem(PurchaseRequest pr) {

        PendingTransactionListItemResponse response =
                new PendingTransactionListItemResponse();

        response.setRequestId(pr.getRequestId());
        response.setBuyerName(pr.getBuyer().getFullName());
        response.setListingTitle(pr.getListing().getTitle());
        response.setTransactionType(pr.getTransactionType().toString());
        response.setCreatedAt(pr.getCreatedAt());
        response.setStatus(pr.getStatus().toString());
        response.setDisplayStatus(getDisplayStatus(pr.getStatus()));

        return response;
    }

    private SellerTransactionDetailResponse
    mapToTransactionDetail(PurchaseRequest pr) {

        SellerTransactionDetailResponse response =
                new SellerTransactionDetailResponse();

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

    private ActionTransactionResponse
    mapToActionResponse(PurchaseRequest pr, String message) {

        ActionTransactionResponse response =
                new ActionTransactionResponse();

        response.setRequestId(pr.getRequestId());
        response.setStatus(pr.getStatus().toString());
        response.setDisplayStatus(getDisplayStatus(pr.getStatus()));
        response.setMessage(message);
        response.setUpdatedAt(pr.getUpdatedAt());

        return response;
    }

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