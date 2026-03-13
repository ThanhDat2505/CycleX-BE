package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.Order;

import java.math.BigDecimal;

public class OrderResponse {

    public Integer orderId;
    public Integer requestId;
    public Integer productId;
    public String productName;
    public Integer listingId;
    public String listingTitle;
    public BigDecimal productPrice;
    public BigDecimal totalAmount;
    public BigDecimal depositAmount;
    public BigDecimal platformFee;
    public BigDecimal inspectionFee;
    public String transactionType;
    public String desiredTransactionTime;
    public String buyerNote;
    public String status;
    public String sellerNote;

    // Buyer info
    public Integer buyerId;
    public String buyerName;
    public String buyerPhone;

    // Seller info
    public Integer sellerId;
    public String sellerName;
    public String sellerPhone;

    // Receiver info
    public String receiverName;
    public String receiverPhone;
    public String receiverAddress;

    public String createdAt;
    public String updatedAt;

    public OrderResponse() {
    }

    public static OrderResponse from(Order order) {
        OrderResponse res = new OrderResponse();
        res.orderId = order.getOrderId();
        res.status = order.getStatus().name();
        res.totalAmount = order.getTotalAmount();
        res.depositAmount = order.getDepositAmount();
        res.platformFee = order.getPlatformFee();
        res.inspectionFee = order.getInspectionFee();
        res.sellerNote = order.getSellerNote();
        res.buyerNote = order.getBuyerNote();
        res.receiverName = order.getReceiverName();
        res.receiverPhone = order.getReceiverPhone();
        res.receiverAddress = order.getReceiverAddress();
        res.createdAt = order.getCreatedAt() != null ? order.getCreatedAt().toString() : null;
        res.updatedAt = order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null;

        if (order.getTransactionType() != null) {
            res.transactionType = order.getTransactionType().name();
        }
        if (order.getDesiredTransactionTime() != null) {
            res.desiredTransactionTime = order.getDesiredTransactionTime().toString();
        }

        if (order.getPurchaseRequest() != null) {
            res.requestId = order.getPurchaseRequest().getRequestId();
        }

        if (order.getProduct() != null) {
            res.productId = order.getProduct().getProductId();
            res.productName = order.getProduct().getName();
            res.productPrice = order.getProduct().getPrice();
            if (order.getProduct().getListing() != null) {
                res.listingId = order.getProduct().getListing().getListingId();
                res.listingTitle = order.getProduct().getListing().getTitle();
            }
        }

        if (order.getBuyer() != null) {
            res.buyerId = order.getBuyer().getUserId();
            res.buyerName = order.getBuyer().getFullName();
            res.buyerPhone = order.getBuyer().getPhone();
        }

        if (order.getSeller() != null) {
            res.sellerId = order.getSeller().getUserId();
            res.sellerName = order.getSeller().getFullName();
            res.sellerPhone = order.getSeller().getPhone();
        }

        return res;
    }
}
