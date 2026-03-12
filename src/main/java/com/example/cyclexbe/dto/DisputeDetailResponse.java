package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.Dispute;
import com.example.cyclexbe.entity.DisputeEvidence;

import java.util.List;

public class DisputeDetailResponse {

    public Integer id;
    public String status;
    public String createdAt;
    public String updatedAt;
    public String reasonCode;
    public String reasonText;
    public String description;
    public String resolutionNote;
    public String resolutionAction;
    public String resolvedAt;

    public ActorDTO assignee;
    public ActorDTO requester;
    public ActorDTO buyer;
    public ActorDTO seller;
    public ListingDTO listing;
    public TransactionDTO transaction;
    public List<EvidenceDTO> evidence;

    public static class ActorDTO {
        public Integer id;
        public String name;
        public String email;
        public String phone;

        public ActorDTO() {
        }

        public ActorDTO(Integer id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }

    public static class ListingDTO {
        public Integer id;
        public String title;
        public String imageUrl;
        public java.math.BigDecimal priceVnd;
        public String status;

        public ListingDTO() {
        }
    }

    public static class TransactionDTO {
        public Integer id;
        public String status;
        public java.math.BigDecimal amountVnd;
        public String createdAt;
        public String updatedAt;

        public TransactionDTO() {
        }
    }

    public static class EvidenceDTO {
        public String type;
        public String url;
        public String text;
        public String name;
        public String uploaderRole;

        public EvidenceDTO() {
        }

        public static EvidenceDTO from(DisputeEvidence e) {
            EvidenceDTO dto = new EvidenceDTO();
            dto.type = e.getType();
            dto.url = e.getUrl();
            dto.text = e.getText();
            dto.name = e.getName();
            dto.uploaderRole = e.getUploaderRole();
            return dto;
        }
    }

    public static DisputeDetailResponse from(Dispute d) {
        DisputeDetailResponse res = new DisputeDetailResponse();
        res.id = d.getDisputeId();
        res.status = d.getStatus().name();
        res.createdAt = d.getCreatedAt() != null ? d.getCreatedAt().toString() : null;
        res.updatedAt = d.getUpdatedAt() != null ? d.getUpdatedAt().toString() : null;
        res.reasonCode = d.getReasonCode() != null ? d.getReasonCode().name() : null;
        res.reasonText = d.getReasonText();
        res.description = d.getContent();
        res.resolutionNote = d.getResolutionNote();
        res.resolutionAction = d.getResolutionAction();
        res.resolvedAt = d.getResolvedAt() != null ? d.getResolvedAt().toString() : null;

        // Requester (buyer who raised the dispute)
        if (d.getRequester() != null) {
            res.requester = new ActorDTO(
                    d.getRequester().getUserId(),
                    d.getRequester().getFullName(),
                    d.getRequester().getEmail(),
                    d.getRequester().getPhone());
            // buyer = requester in this case
            res.buyer = res.requester;
        }

        // Seller
        if (d.getSeller() != null) {
            res.seller = new ActorDTO(
                    d.getSeller().getUserId(),
                    d.getSeller().getFullName(),
                    d.getSeller().getEmail(),
                    d.getSeller().getPhone());
        }

        // Assignee (inspector handling the dispute)
        if (d.getAssignee() != null) {
            res.assignee = new ActorDTO(
                    d.getAssignee().getUserId(),
                    d.getAssignee().getFullName(),
                    d.getAssignee().getEmail(),
                    d.getAssignee().getPhone());
        }

        // Listing info from purchase request -> product -> listing
        if (d.getPurchaseRequest() != null && d.getPurchaseRequest().getProduct() != null) {
            var product = d.getPurchaseRequest().getProduct();
            res.listing = new ListingDTO();
            if (product.getListing() != null) {
                res.listing.id = product.getListing().getListingId();
                res.listing.title = product.getListing().getTitle();
                res.listing.priceVnd = product.getListing().getPrice();
                res.listing.status = product.getListing().getStatus() != null
                        ? product.getListing().getStatus().name()
                        : null;
                // imageUrl is set externally via setListingImageUrl
            }

            // Transaction info
            res.transaction = new TransactionDTO();
            res.transaction.id = d.getPurchaseRequest().getRequestId();
            res.transaction.status = d.getPurchaseRequest().getStatus() != null
                    ? d.getPurchaseRequest().getStatus().name()
                    : null;
            res.transaction.amountVnd = d.getPurchaseRequest().getDepositAmount();
            res.transaction.createdAt = d.getPurchaseRequest().getCreatedAt() != null
                    ? d.getPurchaseRequest().getCreatedAt().toString()
                    : null;
            res.transaction.updatedAt = d.getPurchaseRequest().getUpdatedAt() != null
                    ? d.getPurchaseRequest().getUpdatedAt().toString()
                    : null;
        }

        // Evidence
        if (d.getEvidenceList() != null) {
            res.evidence = d.getEvidenceList().stream()
                    .map(EvidenceDTO::from)
                    .toList();
        }

        return res;
    }
}
