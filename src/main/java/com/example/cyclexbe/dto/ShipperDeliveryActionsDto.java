package com.example.cyclexbe.dto;

/**
 * DTO for delivery actions (F7/F8 - S-61)
 * Indicates what actions shipper can perform on a delivery
 */
public class ShipperDeliveryActionsDto {

    private Boolean canStart;
    private Boolean canConfirm;
    private Boolean canReportFailed;
    private String message;

    public ShipperDeliveryActionsDto() {}

    public ShipperDeliveryActionsDto(
            Boolean canStart,
            Boolean canConfirm,
            Boolean canReportFailed,
            String message) {
        this.canStart = canStart;
        this.canConfirm = canConfirm;
        this.canReportFailed = canReportFailed;
        this.message = message;
    }

    public Boolean getCanStart() { return canStart; }
    public void setCanStart(Boolean canStart) { this.canStart = canStart; }

    public Boolean getCanConfirm() { return canConfirm; }
    public void setCanConfirm(Boolean canConfirm) { this.canConfirm = canConfirm; }

    public Boolean getCanReportFailed() { return canReportFailed; }
    public void setCanReportFailed(Boolean canReportFailed) { this.canReportFailed = canReportFailed; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

