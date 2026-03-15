package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.PricingPreviewDto;
import com.example.cyclexbe.dto.PurchaseRequestCreateRequest;
import com.example.cyclexbe.dto.PurchaseRequestInitResponse;
import com.example.cyclexbe.dto.PurchaseRequestResponse;
import com.example.cyclexbe.dto.PurchaseRequestReviewResponse;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.exception.InvalidListingException;
import com.example.cyclexbe.exception.PurchaseRequestException;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PurchaseRequestService {

    // ===== Constants =====
    private static final int DEPOSIT_RATE_PERCENT = 10;      // 10%
    private static final int NOTE_MAX_LENGTH = 500;

    // TODO: Replace with actual business rules / config if your team defines later
    private static final BigDecimal DEFAULT_PLATFORM_FEE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_INSPECTION_FEE = BigDecimal.ZERO;

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository purchaseRequestRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // =========================================================
    // S-50: INIT SCREEN DATA
    // =========================================================
    @Transactional(readOnly = true)
    public PurchaseRequestInitResponse getInitData(Integer productId, Integer buyerId) {
        Product product = getProductOrThrow(productId);
        BikeListing listing = product.getListing();
        User buyer = getBuyerOrThrow(buyerId);

        User seller = product.getSeller();
        if (seller == null) {
            throw new InvalidListingException("SELLER_NOT_FOUND", "Product seller not found");
        }

        List<String> errors = new ArrayList<>();
        boolean canCreateRequest = true;

        if (!"AVAILABLE".equals(product.getStatus())) {
            canCreateRequest = false;
            errors.add("Product is not available for purchase (Status: " + product.getStatus() + ")");
        }
        if (seller.getUserId().equals(buyerId)) {
            canCreateRequest = false;
            errors.add("You cannot buy your own product");
        }


        BigDecimal productPrice = product.getPrice();

        // Build listing info (using product info where applicable)
        PurchaseRequestInitResponse.ListingInfoDto listingInfo =
                new PurchaseRequestInitResponse.ListingInfoDto(
                        listing.getListingId(),
                        product.getName(), // Use product name
                        productPrice,
                        product.getStatus(),
                        null // TODO: map thumbnail URL if entity has image field
                );

        // Build seller info (mask phone nếu cần)
        PurchaseRequestInitResponse.SellerInfoDto sellerInfo =
                new PurchaseRequestInitResponse.SellerInfoDto(
                        seller.getUserId(),
                        seller.getFullName(),
                        seller.getPhone(),     // TODO: mask phone if BA/UI requires
                        seller.getAvatarUrl()
                );

        // Build buyer info
        PurchaseRequestInitResponse.BuyerInfoDto buyerInfo =
                new PurchaseRequestInitResponse.BuyerInfoDto(
                        buyer.getUserId(),
                        buyer.getFullName(),
                        buyer.getEmail(),
                        buyer.getPhone(),
                        buyer.getAvatarUrl()
                );

        // Pricing preview
        BigDecimal depositAmount = calculateDepositAmount(productPrice);
        BigDecimal platformFee = getPlatformFee(productPrice);
        BigDecimal inspectionFee = getInspectionFee(productPrice);

        PricingPreviewDto pricingPreview = new PricingPreviewDto(
                DEPOSIT_RATE_PERCENT,
                depositAmount,
                platformFee,
                inspectionFee
        );

        // Rules
        PurchaseRequestInitResponse.RulesDto rules =
                new PurchaseRequestInitResponse.RulesDto(
                        NOTE_MAX_LENGTH,
                        PurchaseRequestStatus.PENDING_SELLER_CONFIRM.toString(),
                        DEPOSIT_RATE_PERCENT
                );

        return new PurchaseRequestInitResponse(
                listingInfo,
                sellerInfo,
                buyerInfo,
                pricingPreview,
                canCreateRequest,
                errors,
                rules
        );
    }

    // =========================================================
    // S-50: REVIEW (NO DB WRITE)
    // =========================================================
    @Transactional(readOnly = true)
    public PurchaseRequestReviewResponse reviewPurchaseRequest(
            Integer productId,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        Product product = getProductOrThrow(productId);
        getBuyerOrThrow(buyerId); // validate buyer exists

        // Re-validate business rules at service layer
        validateCreateRequestBusinessRules(product, buyerId, request);

        BigDecimal productPrice = product.getPrice();
        // PURCHASE (Mua ngay) -> depositAmount = full listing price
        // DEPOSIT (Đặt cọc)   -> depositAmount = 10% of listing price
        BigDecimal depositAmount = request.getTransactionType() == TransactionType.PURCHASE
                ? productPrice
                : calculateDepositAmount(productPrice);
        BigDecimal platformFee = getPlatformFee(productPrice);
        BigDecimal inspectionFee = getInspectionFee(productPrice);

        return new PurchaseRequestReviewResponse(
                request.getTransactionType(),
                request.getDesiredTransactionTime(),
                request.getNote(),
                productPrice,
                depositAmount,
                platformFee,
                inspectionFee
        );
    }

    // =========================================================
    // S-50: CREATE / CONFIRM (WRITE DB)
    // =========================================================
    public PurchaseRequestResponse createPurchaseRequest(
            Integer productId,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        // TODO (race condition): if needed, use pessimistic lock query for product
        // ex: productRepository.findByIdForUpdate(productId)

        Product product = getProductOrThrow(productId);
        User buyer = getBuyerOrThrow(buyerId);

        // Re-validate at create step (do not trust FE / review-only validation)
        validateCreateRequestBusinessRules(product, buyerId, request);

        BigDecimal productPrice = product.getPrice();
        // PURCHASE (Mua ngay) -> depositAmount = full listing price
        // DEPOSIT (Đặt cọc)   -> depositAmount = 10% of listing price
        BigDecimal depositAmount = request.getTransactionType() == TransactionType.PURCHASE
                ? productPrice
                : calculateDepositAmount(productPrice);
        BigDecimal platformFee = getPlatformFee(productPrice);
        BigDecimal inspectionFee = getInspectionFee(productPrice);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setProduct(product); // Link to Product
        purchaseRequest.setBuyer(buyer);
        purchaseRequest.setTransactionType(request.getTransactionType());
        purchaseRequest.setDesiredTransactionTime(request.getDesiredTransactionTime());
        purchaseRequest.setNote(trimToNull(request.getNote()));
        purchaseRequest.setDepositAmount(depositAmount);
        purchaseRequest.setPlatformFee(platformFee);
        purchaseRequest.setInspectionFee(inspectionFee);
        purchaseRequest.setStatus(PurchaseRequestStatus.PENDING_SELLER_CONFIRM);

        // Nếu entity chưa auto timestamp thì set tay (tùy entity của bạn)
        // purchaseRequest.setCreatedAt(LocalDateTime.now());

        PurchaseRequest saved = purchaseRequestRepository.save(purchaseRequest);

        return new PurchaseRequestResponse(
                saved.getRequestId(),
                saved.getProduct().getListing().getListingId(), // Keep returning listingId for compatibility or change to productId
                saved.getBuyer().getUserId(),
                saved.getTransactionType(),
                saved.getDesiredTransactionTime(),
                saved.getNote(),
                saved.getProduct().getPrice(),
                saved.getDepositAmount(),
                saved.getPlatformFee(),
                saved.getInspectionFee(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    // =========================================================
    // PRICING HELPERS
    // =========================================================
    public BigDecimal calculateDepositAmount(BigDecimal listingPrice) {
        if (listingPrice == null) {
            throw new PurchaseRequestException("INVALID_PRICE", "Listing price is required");
        }
        if (listingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PurchaseRequestException("INVALID_PRICE", "Listing price must be greater than 0");
        }

        return listingPrice
                .multiply(BigDecimal.valueOf(DEPOSIT_RATE_PERCENT))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public Integer getDepositRatePercent() {
        return DEPOSIT_RATE_PERCENT;
    }

    public BigDecimal getPlatformFee(BigDecimal listingPrice) {
        // TODO: Replace with actual business rules if defined by BA/team
        return DEFAULT_PLATFORM_FEE;
    }

    public BigDecimal getInspectionFee(BigDecimal listingPrice) {
        // TODO: Replace with actual business rules if defined by BA/team
        return DEFAULT_INSPECTION_FEE;
    }

    // =========================================================
    // PRIVATE HELPERS - FETCH
    // =========================================================
    private User getBuyerOrThrow(Integer buyerId) {
        return userRepository.findById(buyerId)
                .orElseThrow(() -> new InvalidListingException(
                        "BUYER_NOT_FOUND",
                        "Buyer with ID " + buyerId + " not found"
                ));
    }

    private Product getProductOrThrow(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new InvalidListingException(
                        "PRODUCT_NOT_FOUND",
                        "Product with ID " + productId + " not found"
                ));
    }

    // =========================================================
    // PRIVATE HELPERS - VALIDATION
    // =========================================================
    private void validateCreateRequestBusinessRules(
            Product product,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        if (request == null) {
            throw new PurchaseRequestException("INVALID_REQUEST", "Request body must not be null");
        }

        if (!"AVAILABLE".equals(product.getStatus())) {
             throw new InvalidListingException(
                    "INVALID_REQUEST",
                    "Cannot create purchase request: Product is not available (Status: " + product.getStatus() + ")"
            );
        }

        if (product.getSeller().getUserId().equals(buyerId)) {
             throw new InvalidListingException(
                    "INVALID_REQUEST",
                    "Cannot create purchase request: You cannot buy your own product"
            );
        }

        if (request.getTransactionType() == null) {
            throw new PurchaseRequestException("INVALID_TRANSACTION_TYPE", "Transaction type is required");
        }

        if (request.getDesiredTransactionTime() == null) {
            throw new PurchaseRequestException("INVALID_TIME", "Desired transaction time is required");
        }

        // Re-validate at service layer (do not rely only on DTO annotations like @Future)
        if (!request.getDesiredTransactionTime().isAfter(LocalDateTime.now())) {
            throw new PurchaseRequestException("INVALID_TIME", "Desired transaction time must be in the future");
        }

        if (request.getNote() != null && request.getNote().length() > NOTE_MAX_LENGTH) {
            throw new PurchaseRequestException("INVALID_NOTE", "Note cannot exceed " + NOTE_MAX_LENGTH + " characters");
        }
    }

    private String trimToNull(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

