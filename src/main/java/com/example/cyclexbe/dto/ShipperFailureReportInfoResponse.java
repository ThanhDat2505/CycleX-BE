package com.example.cyclexbe.dto;

/**
 * DTO for GET /api/shipper/deliveries/{deliveryId}/failure-report
 * Load delivery info before shipper submits failure reason
 */
public class ShipperFailureReportInfoResponse {

    private Integer deliveryId;
    private Integer transactionId;
    private Integer listingId;
    private String buyerName;
    private String buyerPhone;
    private String sellerName;
    private String deliveryAddress;
    private String productName;
    private String deliveryStatus;
    private String transactionStatus;

    public ShipperFailureReportInfoResponse() {}

    public ShipperFailureReportInfoResponse(
            Integer deliveryId, Integer transactionId, Integer listingId,
            String buyerName, String buyerPhone, String sellerName,
            String deliveryAddress, String productName,
            String deliveryStatus, String transactionStatus) {
        this.deliveryId = deliveryId;
        this.transactionId = transactionId;
        this.listingId = listingId;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.sellerName = sellerName;
        this.deliveryAddress = deliveryAddress;
        this.productName = productName;
        this.deliveryStatus = deliveryStatus;
        this.transactionStatus = transactionStatus;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public Integer getListingId() { return listingId; }
    public void setListingId(Integer listingId) { this.listingId = listingId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
}
