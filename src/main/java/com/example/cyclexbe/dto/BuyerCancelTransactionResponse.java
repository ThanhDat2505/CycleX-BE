package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;

/**
 * DTO for S-54: Buyer Cancel Transaction Response
 * POST /api/buyer/transactions/{id}/cancel
 */
public class BuyerCancelTransactionResponse {

    private Integer requestId;
    private PurchaseRequestStatus oldStatus;
    private PurchaseRequestStatus newStatus;
    private BikeListingStatus listingNewStatus;
    private String redirectUrl;

    public BuyerCancelTransactionResponse() {}

    public BuyerCancelTransactionResponse(
            Integer requestId,
            PurchaseRequestStatus oldStatus,
            PurchaseRequestStatus newStatus,
            BikeListingStatus listingNewStatus,
            String redirectUrl) {
        this.requestId = requestId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.listingNewStatus = listingNewStatus;
        this.redirectUrl = redirectUrl;
    }

    // Getters & Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public PurchaseRequestStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(PurchaseRequestStatus oldStatus) { this.oldStatus = oldStatus; }

    public PurchaseRequestStatus getNewStatus() { return newStatus; }
    public void setNewStatus(PurchaseRequestStatus newStatus) { this.newStatus = newStatus; }

    public BikeListingStatus getListingNewStatus() { return listingNewStatus; }
    public void setListingNewStatus(BikeListingStatus listingNewStatus) { this.listingNewStatus = listingNewStatus; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}

