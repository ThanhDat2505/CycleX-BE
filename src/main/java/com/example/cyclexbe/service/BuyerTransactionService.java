package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.dto.BuyerCancelTransactionResponse;
import com.example.cyclexbe.dto.BuyerTransactionActionsDto;
import com.example.cyclexbe.dto.BuyerTransactionDetailResponse;
import com.example.cyclexbe.dto.BuyerTransactionListItemResponse;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.exception.ForbiddenException;
import com.example.cyclexbe.exception.InvalidListingException;
import com.example.cyclexbe.exception.PurchaseRequestException;
import com.example.cyclexbe.repository.ListingImageRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service for S-54: Buyer Transaction Detail
 * Handles buyer-specific transaction operations like viewing details and cancellation
 */
@Service
@Transactional
public class BuyerTransactionService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ProductRepository productRepository;
        private final ListingImageRepository listingImageRepository;

    public BuyerTransactionService(
            PurchaseRequestRepository purchaseRequestRepository,
                        ProductRepository productRepository,
                        ListingImageRepository listingImageRepository) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.productRepository = productRepository;
                this.listingImageRepository = listingImageRepository;
    }

        @Transactional(readOnly = true)
        public List<BuyerTransactionListItemResponse> getBuyerTransactions(Integer buyerId) {
                return purchaseRequestRepository.findByBuyer_UserId(buyerId)
                                .stream()
                                .sorted(Comparator.comparing(PurchaseRequest::getCreatedAt).reversed())
                                .map(pr -> mapToBuyerTransactionItem(pr, buyerId))
                                .toList();
        }

    /**
     * F1: Load transaction detail for buyer
     * GET /api/buyer/transactions/{id}
     *
     * @param requestId The transaction/purchase request ID
     * @param buyerId The buyer ID from authentication
     * @return Transaction detail with seller info, listing info, and available actions
     * @throws PurchaseRequestException if transaction not found (404)
     * @throws ForbiddenException if buyer mismatch (403)
     */
    @Transactional(readOnly = true)
    public BuyerTransactionDetailResponse getTransactionDetail(Integer requestId, Integer buyerId) {
        // First check if transaction exists at all
        Optional<PurchaseRequest> optionalRequest = purchaseRequestRepository.findByRequestId(requestId);

        if (optionalRequest.isEmpty()) {
            throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                    "Transaction not found");
        }

        PurchaseRequest transaction = optionalRequest.get();

        // Check if buyer matches
        if (!transaction.getBuyer().getUserId().equals(buyerId)) {
            throw new ForbiddenException("FORBIDDEN_BUYER_MISMATCH",
                    "You don't have permission to view this transaction");
        }

        // Fetch with eager loading
        Optional<PurchaseRequest> optionalRequestWithEager =
                purchaseRequestRepository.findByRequestIdAndBuyerId(requestId, buyerId);
        if (optionalRequestWithEager.isEmpty()) {
            throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                    "Transaction not found");
        }

        transaction = optionalRequestWithEager.get();
        Product product = transaction.getProduct();
        User seller = product.getSeller();

        if (seller == null) {
            throw new InvalidListingException("SELLER_NOT_FOUND", "Product seller not found");
        }

        // Determine if buyer can cancel
        BuyerTransactionActionsDto actions = determineActions(transaction);

        // Build seller info
        BuyerTransactionDetailResponse.SellerInfoDto sellerInfo =
                new BuyerTransactionDetailResponse.SellerInfoDto(
                        seller.getUserId(),
                        seller.getFullName(),
                        seller.getPhone(),
                        seller.getAvatarUrl()
                );

        // Build product info (thay thế listing info cũ)
        BuyerTransactionDetailResponse.ListingInfoDto productInfo =
                new BuyerTransactionDetailResponse.ListingInfoDto(
                        product.getListing().getListingId(),
                        product.getName(),
                        product.getDescription(),
                        product.getListing().getBikeType(),
                        product.getListing().getBrand(),
                        product.getListing().getModel(),
                        product.getListing().getManufactureYear(),
                        product.getListing().getCondition(),
                        product.getListing().getStatus(),
                        product.getListing().getPickupAddress(),
                        product.getListing().getLocationCity()
                );

        // Build timeline
        BuyerTransactionDetailResponse.TimelineDto timeline =
                new BuyerTransactionDetailResponse.TimelineDto(
                        transaction.getCreatedAt(),
                        transaction.getUpdatedAt()
                );

        // Build response
        BuyerTransactionDetailResponse response = new BuyerTransactionDetailResponse(
                transaction.getRequestId(),
                transaction.getStatus(),
                sellerInfo,
                productInfo,
                product.getPrice(),
                transaction.getDepositAmount(),
                transaction.getPlatformFee(),
                transaction.getInspectionFee(),
                transaction.getNote(),
                transaction.getDesiredTransactionTime(),
                timeline,
                actions,
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

        return response;
    }

    /**
     * F2: Cancel transaction
     * POST /api/buyer/transactions/{id}/cancel
     *
     * Only allowed when transaction.status == PENDING_SELLER_CONFIRM
     * When cancelled:
     *   - transaction.status -> CANCELLED
     *   - listing.status -> APPROVED
     *
     * @param requestId The transaction/purchase request ID
     * @param buyerId The buyer ID from authentication
     * @return Cancel transaction response with status updates and redirect URL
     * @throws PurchaseRequestException if transaction not found (404), or invalid status (409)
     * @throws ForbiddenException if buyer mismatch (403)
     */
    public BuyerCancelTransactionResponse cancelTransaction(Integer requestId, Integer buyerId) {
        // First check if transaction exists at all
        Optional<PurchaseRequest> optionalRequest = purchaseRequestRepository.findByRequestId(requestId);

        if (optionalRequest.isEmpty()) {
            throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                    "Transaction not found");
        }

        PurchaseRequest transaction = optionalRequest.get();

        // Check if buyer matches
        if (!transaction.getBuyer().getUserId().equals(buyerId)) {
            throw new ForbiddenException("FORBIDDEN_BUYER_MISMATCH",
                    "You don't have permission to cancel this transaction");
        }

        // Validate transaction status
        if (transaction.getStatus() != PurchaseRequestStatus.PENDING_SELLER_CONFIRM) {
            throw new PurchaseRequestException("INVALID_TRANSACTION_STATUS",
                    "Transaction can only be cancelled when status is PENDING_SELLER_CONFIRM. " +
                    "Current status: " + transaction.getStatus());
        }

        // Perform atomic cancel operation
        PurchaseRequestStatus oldStatus = transaction.getStatus();

        // Update transaction status
        transaction.setStatus(PurchaseRequestStatus.CANCELLED);
        PurchaseRequest savedTransaction = purchaseRequestRepository.save(transaction);

        // Update product status -> AVAILABLE (thay vì cập nhật listing)
        Product product = transaction.getProduct();
        product.setStatus("AVAILABLE");
        productRepository.save(product);

        // Build and return response
        return new BuyerCancelTransactionResponse(
                savedTransaction.getRequestId(),
                oldStatus,
                savedTransaction.getStatus(),
                null, // không còn trả listingStatus
                "/buyer/transactions"
        );
    }

    /**
     * Determine available actions for buyer on this transaction
     * Currently only supports canCancel action
     */
    private BuyerTransactionActionsDto determineActions(PurchaseRequest transaction) {
        boolean canCancel = transaction.getStatus() == PurchaseRequestStatus.PENDING_SELLER_CONFIRM;
        String cancelDisabledReason = null;

        if (!canCancel) {
            cancelDisabledReason = "Transaction can only be cancelled when status is PENDING_SELLER_CONFIRM. " +
                    "Current status: " + transaction.getStatus();
        }

        return new BuyerTransactionActionsDto(canCancel, cancelDisabledReason);
    }

        private BuyerTransactionListItemResponse mapToBuyerTransactionItem(PurchaseRequest pr, Integer buyerId) {
                Product product = pr.getProduct();
                Integer listingId = product != null && product.getListing() != null
                                ? product.getListing().getListingId()
                                : null;

                String listingImage = null;
                if (product != null && product.getListing() != null) {
                        listingImage = listingImageRepository.findByBikeListingOrderByImageOrder(product.getListing())
                                        .stream()
                                        .map(img -> img.getImagePath())
                                        .filter(path -> path != null && !path.isBlank())
                                        .findFirst()
                                        .orElse(null);
                }

                BigDecimal totalAmount = safeAmount(pr.getDepositAmount())
                                .add(safeAmount(pr.getPlatformFee()))
                                .add(safeAmount(pr.getInspectionFee()));

                return new BuyerTransactionListItemResponse(
                                pr.getRequestId(),
                                buyerId,
                                product != null && product.getSeller() != null ? product.getSeller().getUserId() : null,
                                listingId,
                                product != null ? product.getName() : null,
                                listingImage,
                                product != null && product.getSeller() != null ? product.getSeller().getFullName() : null,
                                product != null && product.getSeller() != null ? product.getSeller().getPhone() : null,
                                pr.getTransactionType() != null ? pr.getTransactionType().name() : null,
                                pr.getStatus() != null ? pr.getStatus().name() : null,
                                totalAmount,
                                pr.getCreatedAt()
                );
        }

        private BigDecimal safeAmount(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }
}
