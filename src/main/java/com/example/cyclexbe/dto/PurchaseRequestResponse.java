package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for purchase request creation endpoint
 * POST /api/v1/listings/{listingId}/purchase-requests
 */
public class PurchaseRequestResponse {

    private Integer requestId;
    private Integer listingId;
    private Integer buyerId;
    private TransactionType transactionType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime desiredTransactionTime;
    private String note;
    private BigDecimal listingPrice;
    private BigDecimal depositAmount;
    private BigDecimal platformFee;
    private BigDecimal inspectionFee;
    private BigDecimal totalAmount;
    private PurchaseRequestStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public PurchaseRequestResponse() {}

    public PurchaseRequestResponse(
            Integer requestId,
            Integer listingId,
            Integer buyerId,
            TransactionType transactionType,
            LocalDateTime desiredTransactionTime,
            String note,
            BigDecimal listingPrice,
            BigDecimal depositAmount,
            BigDecimal platformFee,
            BigDecimal inspectionFee,
            PurchaseRequestStatus status,
            LocalDateTime createdAt) {
        this.requestId = requestId;
        this.listingId = listingId;
        this.buyerId = buyerId;
        this.transactionType = transactionType;
        this.desiredTransactionTime = desiredTransactionTime;
        this.note = note;
        this.listingPrice = listingPrice;
        this.depositAmount = depositAmount;
        this.platformFee = platformFee;
        this.inspectionFee = inspectionFee;
        this.totalAmount = depositAmount.add(platformFee).add(inspectionFee);
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Integer getListingId() { return listingId; }
    public void setListingId(Integer listingId) { this.listingId = listingId; }

    public Integer getBuyerId() { return buyerId; }
    public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public LocalDateTime getDesiredTransactionTime() { return desiredTransactionTime; }
    public void setDesiredTransactionTime(LocalDateTime desiredTransactionTime) { this.desiredTransactionTime = desiredTransactionTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

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

    public PurchaseRequestStatus getStatus() { return status; }
    public void setStatus(PurchaseRequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

