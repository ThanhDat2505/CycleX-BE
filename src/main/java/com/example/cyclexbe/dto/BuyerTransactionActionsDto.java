package com.example.cyclexbe.dto;

/**
 * DTO for buyer transaction actions
 * Indicates what actions are available for the buyer on this transaction
 */
public class BuyerTransactionActionsDto {

    private boolean canCancel;
    private String cancelDisabledReason;

    public BuyerTransactionActionsDto() {}

    public BuyerTransactionActionsDto(boolean canCancel, String cancelDisabledReason) {
        this.canCancel = canCancel;
        this.cancelDisabledReason = cancelDisabledReason;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getCancelDisabledReason() {
        return cancelDisabledReason;
    }

    public void setCancelDisabledReason(String cancelDisabledReason) {
        this.cancelDisabledReason = cancelDisabledReason;
    }
}

