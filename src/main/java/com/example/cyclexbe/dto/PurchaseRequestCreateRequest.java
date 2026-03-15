package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.TransactionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Request DTO for creating/reviewing a purchase request
 */
public class PurchaseRequestCreateRequest {

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Desired transaction time is required")
    @Future(message = "Desired transaction time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime desiredTransactionTime;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    @Size(max = 100, message = "Receiver name must not exceed 100 characters")
    private String receiverName;

    @Size(max = 20, message = "Receiver phone must not exceed 20 characters")
    private String receiverPhone;

    @Size(max = 500, message = "Receiver address must not exceed 500 characters")
    private String receiverAddress;

    public PurchaseRequestCreateRequest() {}

    public PurchaseRequestCreateRequest(
            TransactionType transactionType,
            LocalDateTime desiredTransactionTime,
            String note) {
        this.transactionType = transactionType;
        this.desiredTransactionTime = desiredTransactionTime;
        this.note = note;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getDesiredTransactionTime() {
        return desiredTransactionTime;
    }

    public void setDesiredTransactionTime(LocalDateTime desiredTransactionTime) {
        this.desiredTransactionTime = desiredTransactionTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}

