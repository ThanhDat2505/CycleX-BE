package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.Dispute;

public class DisputeListRowResponse {

    public Integer id;
    public Integer transactionId;
    public Integer listingId;
    public String listingTitle;
    public String status;
    public String reasonText;
    public String createdAt;
    public Integer assigneeId;
    public String assigneeName;
    public String requesterName;

    public DisputeListRowResponse() {
    }

    public static DisputeListRowResponse from(Dispute d) {
        DisputeListRowResponse res = new DisputeListRowResponse();
        res.id = d.getDisputeId();
        res.status = d.getStatus().name();
        res.reasonText = d.getReasonText();
        res.createdAt = d.getCreatedAt() != null ? d.getCreatedAt().toString() : null;

        if (d.getRequester() != null) {
            res.requesterName = d.getRequester().getFullName();
        }

        if (d.getAssignee() != null) {
            res.assigneeId = d.getAssignee().getUserId();
            res.assigneeName = d.getAssignee().getFullName();
        }

        if (d.getPurchaseRequest() != null) {
            res.transactionId = d.getPurchaseRequest().getRequestId();
            if (d.getPurchaseRequest().getProduct() != null
                    && d.getPurchaseRequest().getProduct().getListing() != null) {
                res.listingId = d.getPurchaseRequest().getProduct().getListing().getListingId();
                res.listingTitle = d.getPurchaseRequest().getProduct().getListing().getTitle();
            }
        }

        return res;
    }
}
