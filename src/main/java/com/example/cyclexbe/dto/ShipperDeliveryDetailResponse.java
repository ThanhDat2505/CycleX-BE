package com.example.cyclexbe.dto;

/**
 * DTO for delivery detail response (S-61 F3/F4/F7/F8)
 * GET /api/shipper/deliveries/{deliveryId}
 * Contains full delivery info, seller/buyer contact, pickup/delivery locations, timeline, and actions
 */
public class ShipperDeliveryDetailResponse {

    private Integer deliveryId;
    private Integer orderId;
    private String status;

    private ShipperContactInfoDto seller;
    private ShipperContactInfoDto buyer;

    private ShipperPickupLocationDto pickup;
    private ShipperDeliveryLocationDto delivery;

    private ShipperDeliveryTimelineDto timeline;
    private ShipperDeliveryActionsDto actions;

    public ShipperDeliveryDetailResponse() {}

    public ShipperDeliveryDetailResponse(
            Integer deliveryId,
            Integer orderId,
            String status,
            ShipperContactInfoDto seller,
            ShipperContactInfoDto buyer,
            ShipperPickupLocationDto pickup,
            ShipperDeliveryLocationDto delivery,
            ShipperDeliveryTimelineDto timeline,
            ShipperDeliveryActionsDto actions) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = status;
        this.seller = seller;
        this.buyer = buyer;
        this.pickup = pickup;
        this.delivery = delivery;
        this.timeline = timeline;
        this.actions = actions;
    }

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ShipperContactInfoDto getSeller() { return seller; }
    public void setSeller(ShipperContactInfoDto seller) { this.seller = seller; }

    public ShipperContactInfoDto getBuyer() { return buyer; }
    public void setBuyer(ShipperContactInfoDto buyer) { this.buyer = buyer; }

    public ShipperPickupLocationDto getPickup() { return pickup; }
    public void setPickup(ShipperPickupLocationDto pickup) { this.pickup = pickup; }

    public ShipperDeliveryLocationDto getDelivery() { return delivery; }
    public void setDelivery(ShipperDeliveryLocationDto delivery) { this.delivery = delivery; }

    public ShipperDeliveryTimelineDto getTimeline() { return timeline; }
    public void setTimeline(ShipperDeliveryTimelineDto timeline) { this.timeline = timeline; }

    public ShipperDeliveryActionsDto getActions() { return actions; }
    public void setActions(ShipperDeliveryActionsDto actions) { this.actions = actions; }
}

