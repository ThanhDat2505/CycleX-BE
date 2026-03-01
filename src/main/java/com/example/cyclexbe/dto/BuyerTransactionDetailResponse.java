package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for S-54: Buyer Transaction Detail Response
 * GET /api/buyer/transactions/{id}
 */
public class BuyerTransactionDetailResponse {

    private Integer requestId;
    private PurchaseRequestStatus status;

    // Seller info
    private SellerInfoDto seller;

    // Listing info
    private ListingInfoDto listing;

    // Transaction fees and amounts
    private BigDecimal listingPrice;
    private BigDecimal depositAmount;
    private BigDecimal platformFee;
    private BigDecimal inspectionFee;
    private BigDecimal totalAmount;

    // Transaction details
    private String note;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime desiredTransactionTime;

    // Timeline (minimal)
    private TimelineDto timeline;

    // Actions available
    private BuyerTransactionActionsDto actions;

    // Timestamps
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public BuyerTransactionDetailResponse() {}

    public BuyerTransactionDetailResponse(
            Integer requestId,
            PurchaseRequestStatus status,
            SellerInfoDto seller,
            ListingInfoDto listing,
            BigDecimal listingPrice,
            BigDecimal depositAmount,
            BigDecimal platformFee,
            BigDecimal inspectionFee,
            String note,
            LocalDateTime desiredTransactionTime,
            TimelineDto timeline,
            BuyerTransactionActionsDto actions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.status = status;
        this.seller = seller;
        this.listing = listing;
        this.listingPrice = listingPrice;
        this.depositAmount = depositAmount;
        this.platformFee = platformFee;
        this.inspectionFee = inspectionFee;
        this.totalAmount = depositAmount.add(platformFee).add(inspectionFee);
        this.note = note;
        this.desiredTransactionTime = desiredTransactionTime;
        this.timeline = timeline;
        this.actions = actions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public PurchaseRequestStatus getStatus() { return status; }
    public void setStatus(PurchaseRequestStatus status) { this.status = status; }

    public SellerInfoDto getSeller() { return seller; }
    public void setSeller(SellerInfoDto seller) { this.seller = seller; }

    public ListingInfoDto getListing() { return listing; }
    public void setListing(ListingInfoDto listing) { this.listing = listing; }

    public BigDecimal getListingPrice() { return listingPrice; }
    public void setListingPrice(BigDecimal listingPrice) { this.listingPrice = listingPrice; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }

    public BigDecimal getInspectionFee() { return inspectionFee; }
    public void setInspectionFee(BigDecimal inspectionFee) { this.inspectionFee = inspectionFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getDesiredTransactionTime() { return desiredTransactionTime; }
    public void setDesiredTransactionTime(LocalDateTime desiredTransactionTime) { this.desiredTransactionTime = desiredTransactionTime; }

    public TimelineDto getTimeline() { return timeline; }
    public void setTimeline(TimelineDto timeline) { this.timeline = timeline; }

    public BuyerTransactionActionsDto getActions() { return actions; }
    public void setActions(BuyerTransactionActionsDto actions) { this.actions = actions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Inner DTO classes

    /**
     * Seller information in transaction detail
     */
    public static class SellerInfoDto {
        private Integer userId;
        private String fullName;
        private String phone;
        private String avatarUrl;

        public SellerInfoDto() {}

        public SellerInfoDto(Integer userId, String fullName, String phone, String avatarUrl) {
            this.userId = userId;
            this.fullName = fullName;
            this.phone = phone;
            this.avatarUrl = avatarUrl;
        }

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    /**
     * Listing information in transaction detail
     */
    public static class ListingInfoDto {
        private Integer listingId;
        private String title;
        private String description;
        private String bikeType;
        private String brand;
        private String model;
        private Integer manufactureYear;
        private String condition;
        private BikeListingStatus status;
        private String pickupAddress;
        private String locationCity;

        public ListingInfoDto() {}

        public ListingInfoDto(
                Integer listingId,
                String title,
                String description,
                String bikeType,
                String brand,
                String model,
                Integer manufactureYear,
                String condition,
                BikeListingStatus status,
                String pickupAddress,
                String locationCity) {
            this.listingId = listingId;
            this.title = title;
            this.description = description;
            this.bikeType = bikeType;
            this.brand = brand;
            this.model = model;
            this.manufactureYear = manufactureYear;
            this.condition = condition;
            this.status = status;
            this.pickupAddress = pickupAddress;
            this.locationCity = locationCity;
        }

        public Integer getListingId() { return listingId; }
        public void setListingId(Integer listingId) { this.listingId = listingId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getBikeType() { return bikeType; }
        public void setBikeType(String bikeType) { this.bikeType = bikeType; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public Integer getManufactureYear() { return manufactureYear; }
        public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public BikeListingStatus getStatus() { return status; }
        public void setStatus(BikeListingStatus status) { this.status = status; }

        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

        public String getLocationCity() { return locationCity; }
        public void setLocationCity(String locationCity) { this.locationCity = locationCity; }
    }

    /**
     * Timeline information (minimal)
     */
    public static class TimelineDto {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime lastUpdatedAt;

        public TimelineDto() {}

        public TimelineDto(LocalDateTime createdAt, LocalDateTime lastUpdatedAt) {
            this.createdAt = createdAt;
            this.lastUpdatedAt = lastUpdatedAt;
        }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
        public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    }
}

