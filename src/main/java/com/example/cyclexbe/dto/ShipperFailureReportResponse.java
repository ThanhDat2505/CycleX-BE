package com.example.cyclexbe.dto;

/**
 * DTO for POST /api/shipper/deliveries/{deliveryId}/failure-report response
 */
public class ShipperFailureReportResponse {

    private String message;
    private Integer deliveryId;
    private String deliveryStatus;
    private String transactionStatus;

    public ShipperFailureReportResponse() {}

    public ShipperFailureReportResponse(String message, Integer deliveryId,
                                        String deliveryStatus, String transactionStatus) {
        this.message = message;
        this.deliveryId = deliveryId;
        this.deliveryStatus = deliveryStatus;
        this.transactionStatus = transactionStatus;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
}
