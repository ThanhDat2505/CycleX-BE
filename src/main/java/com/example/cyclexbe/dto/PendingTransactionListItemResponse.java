package com.example.cyclexbe.dto;

import java.time.LocalDateTime;

/**
 * DTO for a single pending transaction item in the list
 * Corresponds to S-52 UI row
 */
public class PendingTransactionListItemResponse {

    private Integer requestId;
    private String buyerName;
    private String listingTitle;
    private String transactionType;
    private LocalDateTime createdAt;
    private String status;
    private String displayStatus;

    // No-args constructor
    public PendingTransactionListItemResponse() {
    }

    // All-args constructor
    public PendingTransactionListItemResponse(
            Integer requestId,
            String buyerName,
            String listingTitle,
            String transactionType,
            LocalDateTime createdAt,
            String status,
            String displayStatus) {
        this.requestId = requestId;
        this.buyerName = buyerName;
        this.listingTitle = listingTitle;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.status = status;
        this.displayStatus = displayStatus;
    }

    // Getters
    public Integer getRequestId() {
        return requestId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    // Setters
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}

