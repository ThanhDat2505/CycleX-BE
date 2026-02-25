package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for detailed view of a transaction (S-53)
 */
public class SellerTransactionDetailResponse {

    private Integer requestId;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String listingTitle;
    private Integer listingId;
    private String transactionType;
    private BigDecimal depositAmount;
    private BigDecimal platformFee;
    private BigDecimal inspectionFee;
    private String status;
    private String displayStatus;
    private String note;
    private LocalDateTime desiredTransactionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-args constructor
    public SellerTransactionDetailResponse() {
    }

    // All-args constructor
    public SellerTransactionDetailResponse(
            Integer requestId,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String listingTitle,
            Integer listingId,
            String transactionType,
            BigDecimal depositAmount,
            BigDecimal platformFee,
            BigDecimal inspectionFee,
            String status,
            String displayStatus,
            String note,
            LocalDateTime desiredTransactionTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
        this.listingTitle = listingTitle;
        this.listingId = listingId;
        this.transactionType = transactionType;
        this.depositAmount = depositAmount;
        this.platformFee = platformFee;
        this.inspectionFee = inspectionFee;
        this.status = status;
        this.displayStatus = displayStatus;
        this.note = note;
        this.desiredTransactionTime = desiredTransactionTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Integer getRequestId() {
        return requestId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public Integer getListingId() {
        return listingId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public BigDecimal getInspectionFee() {
        return inspectionFee;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getDesiredTransactionTime() {
        return desiredTransactionTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public void setInspectionFee(BigDecimal inspectionFee) {
        this.inspectionFee = inspectionFee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDesiredTransactionTime(LocalDateTime desiredTransactionTime) {
        this.desiredTransactionTime = desiredTransactionTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

