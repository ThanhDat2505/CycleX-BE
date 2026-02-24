package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.dto.PricingPreviewDto;
import com.example.cyclexbe.dto.PurchaseRequestCreateRequest;
import com.example.cyclexbe.dto.PurchaseRequestInitResponse;
import com.example.cyclexbe.dto.PurchaseRequestResponse;
import com.example.cyclexbe.dto.PurchaseRequestReviewResponse;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.exception.InvalidListingException;
import com.example.cyclexbe.exception.PurchaseRequestException;
import com.example.cyclexbe.repository.BikeListingRepository;
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
    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository purchaseRequestRepository,
            BikeListingRepository bikeListingRepository,
            UserRepository userRepository
    ) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // S-50: INIT SCREEN DATA
    // =========================================================
    @Transactional(readOnly = true)
    public PurchaseRequestInitResponse getInitData(Integer listingId, Integer buyerId) {
        BikeListing listing = getListingOrThrow(listingId);
        User buyer = getBuyerOrThrow(buyerId);

        List<String> errors = new ArrayList<>();
        boolean canCreateRequest = validateCanCreateRequest(listing, buyerId, errors);

        User seller = listing.getSeller();
        if (seller == null) {
            throw new InvalidListingException("SELLER_NOT_FOUND", "Listing seller not found");
        }

        BigDecimal listingPrice = requireListingPrice(listing);

        // Build listing info
        PurchaseRequestInitResponse.ListingInfoDto listingInfo =
                new PurchaseRequestInitResponse.ListingInfoDto(
                        listing.getListingId(),
                        listing.getTitle(),
                        listingPrice,
                        String.valueOf(listing.getStatus()),
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
        BigDecimal depositAmount = calculateDepositAmount(listingPrice);
        BigDecimal platformFee = getPlatformFee(listingPrice);
        BigDecimal inspectionFee = getInspectionFee(listingPrice);

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
            Integer listingId,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        BikeListing listing = getListingOrThrow(listingId);
        getBuyerOrThrow(buyerId); // validate buyer exists

        // Re-validate business rules at service layer
        validateCreateRequestBusinessRules(listing, buyerId, request);

        BigDecimal listingPrice = requireListingPrice(listing);
        BigDecimal depositAmount = calculateDepositAmount(listingPrice);
        BigDecimal platformFee = getPlatformFee(listingPrice);
        BigDecimal inspectionFee = getInspectionFee(listingPrice);

        return new PurchaseRequestReviewResponse(
                request.getTransactionType(),
                request.getDesiredTransactionTime(),
                request.getNote(),
                listingPrice,
                depositAmount,
                platformFee,
                inspectionFee
        );
    }

    // =========================================================
    // S-50: CREATE / CONFIRM (WRITE DB)
    // =========================================================
    public PurchaseRequestResponse createPurchaseRequest(
            Integer listingId,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        // TODO (race condition): if needed, use pessimistic lock query for listing
        // ex: bikeListingRepository.findByIdForUpdate(listingId)

        BikeListing listing = getListingOrThrow(listingId);
        User buyer = getBuyerOrThrow(buyerId);

        // Re-validate at create step (do not trust FE / review-only validation)
        validateCreateRequestBusinessRules(listing, buyerId, request);

        BigDecimal listingPrice = requireListingPrice(listing);
        BigDecimal depositAmount = calculateDepositAmount(listingPrice);
        BigDecimal platformFee = getPlatformFee(listingPrice);
        BigDecimal inspectionFee = getInspectionFee(listingPrice);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setListing(listing);
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
                saved.getListing().getListingId(),
                saved.getBuyer().getUserId(),
                saved.getTransactionType(),
                saved.getDesiredTransactionTime(),
                saved.getNote(),
                saved.getListing().getPrice(),
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
    private BikeListing getListingOrThrow(Integer listingId) {
        return bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new InvalidListingException(
                        "LISTING_NOT_FOUND",
                        "Listing with ID " + listingId + " not found"
                ));
    }

    private User getBuyerOrThrow(Integer buyerId) {
        return userRepository.findById(buyerId)
                .orElseThrow(() -> new InvalidListingException(
                        "BUYER_NOT_FOUND",
                        "Buyer with ID " + buyerId + " not found"
                ));
    }

    private BigDecimal requireListingPrice(BikeListing listing) {
        if (listing.getPrice() == null) {
            throw new PurchaseRequestException("INVALID_PRICE", "Listing price is missing");
        }
        if (listing.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PurchaseRequestException("INVALID_PRICE", "Listing price must be greater than 0");
        }
        return listing.getPrice();
    }

    // =========================================================
    // PRIVATE HELPERS - VALIDATION
    // =========================================================
    private void validateCreateRequestBusinessRules(
            BikeListing listing,
            Integer buyerId,
            PurchaseRequestCreateRequest request
    ) {
        if (request == null) {
            throw new PurchaseRequestException("INVALID_REQUEST", "Request body must not be null");
        }

        List<String> errors = new ArrayList<>();
        if (!validateCanCreateRequest(listing, buyerId, errors)) {
            throw new InvalidListingException(
                    "INVALID_REQUEST",
                    "Cannot create purchase request: " + String.join(", ", errors)
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
            throw new PurchaseRequestException(
                    "NOTE_TOO_LONG",
                    "Note must not exceed " + NOTE_MAX_LENGTH + " characters"
            );
        }
    }

    /**
     * Validate if buyer can create purchase request for this listing.
     *
     * Rules:
     * 1) Listing must be APPROVED
     * 2) Listing must not be DELETED/ARCHIVED (if enum ARCHIVED exists)
     * 3) Buyer must not be the seller
     * 4) TODO: listing must not have conflicting active request(s)
     */
    private boolean validateCanCreateRequest(BikeListing listing, Integer buyerId, List<String> errors) {
        if (listing == null) {
            errors.add("Listing is null");
            return false;
        }

        if (listing.getSeller() == null) {
            errors.add("Listing seller not found");
            return false;
        }

        BikeListingStatus status = listing.getStatus();
        if (status == null) {
            errors.add("Listing status is missing");
            return false;
        }

        // Check hard-invalid statuses first (more specific message)
        if (status == BikeListingStatus.DELETED) {
            errors.add("Listing has been deleted");
            return false;
        }

        // ===== Nếu enum của bạn CÓ ARCHIVED thì mở dòng dưới =====
        // if (status == BikeListingStatus.ARCHIVED) {
        //     errors.add("Listing has been archived");
        //     return false;
        // }

        // Must be APPROVED for S-50
        if (status != BikeListingStatus.APPROVED) {
            errors.add("Listing status must be APPROVED (current: " + status + ")");
            return false;
        }

        // Buyer cannot buy own listing
        if (listing.getSeller().getUserId() != null && listing.getSeller().getUserId().equals(buyerId)) {
            errors.add("You cannot create a purchase request for your own listing");
            return false;
        }

        // TODO: Conflict rule (recommended)
        // Example (same buyer + same listing + active request):
        // boolean exists = purchaseRequestRepository.existsByListing_ListingIdAndBuyer_UserIdAndStatusIn(
        //         listing.getListingId(),
        //         buyerId,
        //         List.of(
        //             PurchaseRequestStatus.PENDING_SELLER_CONFIRM,
        //             PurchaseRequestStatus.SELLER_CONFIRMED
        //         )
        // );
        // if (exists) {
        //     errors.add("You already have an active request for this listing");
        //     return false;
        // }

        return true;
    }

    // =========================================================
    // UTILS
    // =========================================================
    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}