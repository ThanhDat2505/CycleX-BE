package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for purchase request review endpoint
 * POST /api/v1/listings/{listingId}/purchase-requests/review
 */
public class PurchaseRequestReviewResponse {

    private TransactionType transactionType;
    private LocalDateTime desiredTransactionTime;
    private String note;
    private BigDecimal listingPrice;
    private BigDecimal depositAmount;
    private BigDecimal platformFee;
    private BigDecimal inspectionFee;
    private BigDecimal totalAmount;  // depositAmount + platformFee + inspectionFee
    private String statusAfterConfirm;  // PENDING_SELLER_CONFIRM

    public PurchaseRequestReviewResponse() {}

    public PurchaseRequestReviewResponse(
            TransactionType transactionType,
            LocalDateTime desiredTransactionTime,
            String note,
            BigDecimal listingPrice,
            BigDecimal depositAmount,
            BigDecimal platformFee,
            BigDecimal inspectionFee) {
        this.transactionType = transactionType;
        this.desiredTransactionTime = desiredTransactionTime;
        this.note = note;
        this.listingPrice = listingPrice;
        this.depositAmount = depositAmount;
        this.platformFee = platformFee;
        this.inspectionFee = inspectionFee;
        this.totalAmount = depositAmount.add(platformFee).add(inspectionFee);
        this.statusAfterConfirm = PurchaseRequestStatus.PENDING_SELLER_CONFIRM.toString();
    }

    // Getters & Setters
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

    public String getStatusAfterConfirm() { return statusAfterConfirm; }
    public void setStatusAfterConfirm(String statusAfterConfirm) { this.statusAfterConfirm = statusAfterConfirm; }
}

