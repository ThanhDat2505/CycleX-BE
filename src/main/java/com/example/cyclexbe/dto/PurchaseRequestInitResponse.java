package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for purchase request init endpoint
 * GET /api/v1/listings/{listingId}/purchase-request/init
 */
public class PurchaseRequestInitResponse {

    private ListingInfoDto listing;
    private SellerInfoDto seller;
    private BuyerInfoDto buyer;
    private PricingPreviewDto pricingPreview;
    private Boolean canCreateRequest;
    private List<String> errors;  // Validation errors if canCreateRequest = false
    private RulesDto rules;

    public PurchaseRequestInitResponse() {}

    public PurchaseRequestInitResponse(
            ListingInfoDto listing,
            SellerInfoDto seller,
            BuyerInfoDto buyer,
            PricingPreviewDto pricingPreview,
            Boolean canCreateRequest,
            List<String> errors,
            RulesDto rules) {
        this.listing = listing;
        this.seller = seller;
        this.buyer = buyer;
        this.pricingPreview = pricingPreview;
        this.canCreateRequest = canCreateRequest;
        this.errors = errors;
        this.rules = rules;
    }

    // Getters & Setters
    public ListingInfoDto getListing() {
        return listing;
    }

    public void setListing(ListingInfoDto listing) {
        this.listing = listing;
    }

    public SellerInfoDto getSeller() {
        return seller;
    }

    public void setSeller(SellerInfoDto seller) {
        this.seller = seller;
    }

    public BuyerInfoDto getBuyer() {
        return buyer;
    }

    public void setBuyer(BuyerInfoDto buyer) {
        this.buyer = buyer;
    }

    public PricingPreviewDto getPricingPreview() {
        return pricingPreview;
    }

    public void setPricingPreview(PricingPreviewDto pricingPreview) {
        this.pricingPreview = pricingPreview;
    }

    public Boolean getCanCreateRequest() {
        return canCreateRequest;
    }

    public void setCanCreateRequest(Boolean canCreateRequest) {
        this.canCreateRequest = canCreateRequest;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public RulesDto getRules() {
        return rules;
    }

    public void setRules(RulesDto rules) {
        this.rules = rules;
    }

    // Inner DTOs
    public static class ListingInfoDto {
        private Integer listingId;
        private String title;
        private BigDecimal price;
        private String status;  // APPROVED, DRAFT, etc.
        private String thumbnail;  // URL or file key

        public ListingInfoDto() {}

        public ListingInfoDto(Integer listingId, String title, BigDecimal price, String status, String thumbnail) {
            this.listingId = listingId;
            this.title = title;
            this.price = price;
            this.status = status;
            this.thumbnail = thumbnail;
        }

        public Integer getListingId() { return listingId; }
        public void setListingId(Integer listingId) { this.listingId = listingId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    }

    public static class SellerInfoDto {
        private Integer sellerId;
        private String sellerName;
        private String sellerPhone;
        private String avatarUrl;

        public SellerInfoDto() {}

        public SellerInfoDto(Integer sellerId, String sellerName, String sellerPhone, String avatarUrl) {
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.sellerPhone = sellerPhone;
            this.avatarUrl = avatarUrl;
        }

        public Integer getSellerId() { return sellerId; }
        public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }

        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public static class BuyerInfoDto {
        private Integer buyerId;
        private String buyerName;
        private String buyerEmail;
        private String buyerPhone;
        private String avatarUrl;

        public BuyerInfoDto() {}

        public BuyerInfoDto(Integer buyerId, String buyerName, String buyerEmail, String buyerPhone, String avatarUrl) {
            this.buyerId = buyerId;
            this.buyerName = buyerName;
            this.buyerEmail = buyerEmail;
            this.buyerPhone = buyerPhone;
            this.avatarUrl = avatarUrl;
        }

        public Integer getBuyerId() { return buyerId; }
        public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }

        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

        public String getBuyerEmail() { return buyerEmail; }
        public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

        public String getBuyerPhone() { return buyerPhone; }
        public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public static class RulesDto {
        private Integer noteMaxLength;
        private String requestStatusAfterConfirm;
        private Integer depositRatePercent;

        public RulesDto() {}

        public RulesDto(Integer noteMaxLength, String requestStatusAfterConfirm, Integer depositRatePercent) {
            this.noteMaxLength = noteMaxLength;
            this.requestStatusAfterConfirm = requestStatusAfterConfirm;
            this.depositRatePercent = depositRatePercent;
        }

        public Integer getNoteMaxLength() { return noteMaxLength; }
        public void setNoteMaxLength(Integer noteMaxLength) { this.noteMaxLength = noteMaxLength; }

        public String getRequestStatusAfterConfirm() { return requestStatusAfterConfirm; }
        public void setRequestStatusAfterConfirm(String requestStatusAfterConfirm) { this.requestStatusAfterConfirm = requestStatusAfterConfirm; }

        public Integer getDepositRatePercent() { return depositRatePercent; }
        public void setDepositRatePercent(Integer depositRatePercent) { this.depositRatePercent = depositRatePercent; }
    }
}

