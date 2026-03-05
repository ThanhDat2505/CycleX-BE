package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for buyer transaction list item.
 * Used by GET /api/buyer/transactions.
 */
public class BuyerTransactionListItemResponse {

    private Integer requestId;
    private Integer buyerId;
    private Integer sellerId;
    private Integer listingId;
    private String listingTitle;
    private String listingImage;
    private String sellerName;
    private String sellerPhone;
    private String transactionType;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public BuyerTransactionListItemResponse() {
    }

    public BuyerTransactionListItemResponse(
            Integer requestId,
            Integer buyerId,
            Integer sellerId,
            Integer listingId,
            String listingTitle,
            String listingImage,
            String sellerName,
            String sellerPhone,
            String transactionType,
            String status,
            BigDecimal totalAmount,
            LocalDateTime createdAt
    ) {
        this.requestId = requestId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.listingId = listingId;
        this.listingTitle = listingTitle;
        this.listingImage = listingImage;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.transactionType = transactionType;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public String getListingImage() {
        return listingImage;
    }

    public void setListingImage(String listingImage) {
        this.listingImage = listingImage;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
