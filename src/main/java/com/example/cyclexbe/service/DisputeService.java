package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.DisputeReasonCode;
import com.example.cyclexbe.domain.enums.DisputeStatus;
import com.example.cyclexbe.domain.enums.NotificationType;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.*;
import com.example.cyclexbe.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final UserRepository userRepository;
    private final ListingImageRepository listingImageRepository;
    private final NotificationService notificationService;

    public DisputeService(DisputeRepository disputeRepository,
            PurchaseRequestRepository purchaseRequestRepository,
            UserRepository userRepository,
            ListingImageRepository listingImageRepository,
            NotificationService notificationService) {
        this.disputeRepository = disputeRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.userRepository = userRepository;
        this.listingImageRepository = listingImageRepository;
        this.notificationService = notificationService;
    }

    /**
     * Get available dispute reasons
     */
    public List<DisputeReasonResponse> getDisputeReasons() {
        return Arrays.stream(DisputeReasonCode.values())
                .map(code -> {
                    String title;
                    String description;
                    switch (code) {
                        case ITEM_NOT_AS_DESCRIBED:
                            title = "Sản phẩm không đúng mô tả";
                            description = "Xe có hư hỏng không được nêu trong tin đăng";
                            break;
                        case MISSING_DOCUMENTS:
                            title = "Người bán không bàn giao giấy tờ";
                            description = "Giấy tờ xe không đầy đủ hoặc bị giả mạo";
                            break;
                        case MECHANICAL_FAILURE:
                            title = "Lỗi động cơ/kỹ thuật nghiêm trọng";
                            description = "Xe không hoạt động được ngay sau khi nhận";
                            break;
                        case DELIVERY_FAILED:
                            title = "Giao hàng thất bại";
                            description = "Đơn hàng giao hàng không thành công";
                            break;
                        default:
                            title = "Khác";
                            description = "Lý do khác";
                            break;
                    }
                    return new DisputeReasonResponse(code.ordinal() + 1, title, description);
                })
                .toList();
    }

    /**
     * Check if buyer is eligible to create a dispute for an order
     */
    public boolean checkEligibility(Integer buyerId, Integer requestId) {
        PurchaseRequest pr = purchaseRequestRepository.findById(requestId).orElse(null);
        if (pr == null)
            return false;

        // Must be COMPLETED status
        if (pr.getStatus() != PurchaseRequestStatus.COMPLETED)
            return false;

        // Must not already have a dispute
        if (disputeRepository.existsByPurchaseRequest_RequestId(requestId))
            return false;

        // Must be the buyer of this order
        if (pr.getBuyer() == null || !pr.getBuyer().getUserId().equals(buyerId))
            return false;

        return true;
    }

    /**
     * Create a new dispute (buyer action)
     */
    @Transactional
    public DisputeDetailResponse createDispute(CreateDisputeRequest req) {
        // Validate purchase request exists
        PurchaseRequest pr = purchaseRequestRepository.findById(req.orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại"));

        // Must be COMPLETED to dispute
        if (pr.getStatus() != PurchaseRequestStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể khiếu nại đơn hàng đã hoàn thành");
        }

        // Check no existing dispute
        if (disputeRepository.existsByPurchaseRequest_RequestId(req.orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng này đã có khiếu nại");
        }

        User buyer = userRepository.findById(req.buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người mua không tồn tại"));

        // Verify buyer is owner of transaction
        if (!pr.getBuyer().getUserId().equals(req.buyerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không phải người mua của đơn hàng này");
        }

        User seller = userRepository.findById(req.sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người bán không tồn tại"));

        // Map reason
        DisputeReasonCode reasonCode = mapReasonId(req.reasonId);

        Dispute dispute = new Dispute();
        dispute.setPurchaseRequest(pr);
        dispute.setRequester(buyer);
        dispute.setSeller(seller);
        dispute.setTitle(req.title);
        dispute.setContent(req.content);
        dispute.setReasonCode(reasonCode);
        dispute.setReasonText(getReasonTitle(reasonCode));
        dispute.setStatus(DisputeStatus.OPEN);

        // Update purchase request status to DISPUTED
        pr.setStatus(PurchaseRequestStatus.DISPUTED);
        purchaseRequestRepository.save(pr);

        Dispute saved = disputeRepository.save(dispute);

        // Save evidence
        if (req.evidenceUrls != null && !req.evidenceUrls.isEmpty()) {
            for (String url : req.evidenceUrls) {
                DisputeEvidence evidence = new DisputeEvidence();
                evidence.setDispute(saved);
                evidence.setType("IMAGE");
                evidence.setUrl(url);
                evidence.setUploaderRole("BUYER");
                saved.getEvidenceList().add(evidence);
            }
            saved = disputeRepository.save(saved);
        }

        // Notify seller about dispute
        notificationService.createNotification(
                seller,
                "Khiếu nại mới",
                "Đơn hàng #" + pr.getRequestId() + " đã bị khiếu nại bởi người mua. Lý do: " + dispute.getReasonText(),
                NotificationType.SYSTEM,
                "DISPUTE",
                saved.getDisputeId(),
                "/disputes/" + saved.getDisputeId());

        return buildDetailResponse(saved);
    }

    /**
     * Get paginated list of disputes with optional status filter and search
     */
    @Transactional(readOnly = true)
    public Page<DisputeListRowResponse> getDisputes(String status, String search, String sortBy, String sortDir,
            int page, int pageSize) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy != null ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortField));

        DisputeStatus statusEnum = null;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                statusEnum = DisputeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status, return all
            }
        }

        Page<Dispute> disputes = disputeRepository.findByFilters(statusEnum, search, pageable);
        return disputes.map(DisputeListRowResponse::from);
    }

    /**
     * Get dispute detail by ID
     */
    @Transactional(readOnly = true)
    public DisputeDetailResponse getDisputeDetail(Integer disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khiếu nại không tồn tại"));

        return buildDetailResponse(dispute);
    }

    /**
     * Get dispute by buyer (for buyer viewing their dispute result)
     */
    @Transactional(readOnly = true)
    public DisputeDetailResponse getDisputeByIdForBuyer(Integer disputeId) {
        return getDisputeDetail(disputeId);
    }

    /**
     * Resolve a dispute (inspector/admin action)
     */
    @Transactional
    public DisputeDetailResponse resolveDispute(Integer disputeId, ResolveDisputeRequest req) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khiếu nại không tồn tại"));

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khiếu nại đã được xử lý");
        }

        // Validate action
        String action = req.action;
        if (!"REFUND_BUYER".equals(action) && !"RELEASE_FUND_SELLER".equals(action) && !"CLOSE_CASE".equals(action)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hành động không hợp lệ: " + action);
        }

        // Determine new status based on action
        DisputeStatus newStatus;
        if ("CLOSE_CASE".equals(action)) {
            newStatus = DisputeStatus.REJECTED;
        } else {
            newStatus = DisputeStatus.RESOLVED;
        }

        dispute.setStatus(newStatus);
        dispute.setResolutionAction(action);
        dispute.setResolutionNote(req.resolutionNote);
        dispute.setResolvedAt(LocalDateTime.now());

        // Set assignee from current auth context if not already set
        try {
            String authUserId = com.example.cyclexbe.security.SecurityUtils.getAuthenticatedUserId();
            if (dispute.getAssignee() == null) {
                userRepository.findById(Integer.parseInt(authUserId))
                        .ifPresent(dispute::setAssignee);
            }
        } catch (Exception ignored) {
            // Fallback: leave assignee as-is
        }

        Dispute saved = disputeRepository.save(dispute);

        // Notify buyer about resolution
        String statusVi = newStatus == DisputeStatus.RESOLVED ? "đã được giải quyết" : "đã bị từ chối";
        notificationService.createNotification(
                dispute.getRequester(),
                "Kết quả khiếu nại",
                "Khiếu nại #" + disputeId + " " + statusVi + ". " + req.resolutionNote,
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        // Notify seller
        notificationService.createNotification(
                dispute.getSeller(),
                "Cập nhật khiếu nại",
                "Khiếu nại #" + disputeId + " " + statusVi + ".",
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        return buildDetailResponse(saved);
    }

    /**
     * Count disputes by status (for inspector dashboard)
     */
    public long countByStatus(DisputeStatus status) {
        return disputeRepository.countByStatus(status);
    }

    /**
     * Count all open disputes
     */
    public long countOpenDisputes() {
        return disputeRepository.countByStatus(DisputeStatus.OPEN)
                + disputeRepository.countByStatus(DisputeStatus.IN_PROGRESS);
    }

    // --- Helper methods ---

    private DisputeDetailResponse buildDetailResponse(Dispute dispute) {
        DisputeDetailResponse res = DisputeDetailResponse.from(dispute);

        // Set listing image from ListingImageRepository
        if (dispute.getPurchaseRequest() != null
                && dispute.getPurchaseRequest().getProduct() != null
                && dispute.getPurchaseRequest().getProduct().getListing() != null) {
            BikeListing listing = dispute.getPurchaseRequest().getProduct().getListing();
            List<ListingImage> images = listingImageRepository.findByBikeListingOrderByImageOrder(listing);
            if (!images.isEmpty() && res.listing != null) {
                res.listing.imageUrl = images.get(0).getImagePath();
            }
        }

        return res;
    }

    private DisputeReasonCode mapReasonId(Integer reasonId) {
        if (reasonId == null)
            return DisputeReasonCode.OTHER;
        DisputeReasonCode[] codes = DisputeReasonCode.values();
        int index = reasonId - 1;
        if (index >= 0 && index < codes.length) {
            return codes[index];
        }
        return DisputeReasonCode.OTHER;
    }

    private String getReasonTitle(DisputeReasonCode code) {
        switch (code) {
            case ITEM_NOT_AS_DESCRIBED:
                return "Sản phẩm không đúng mô tả";
            case MISSING_DOCUMENTS:
                return "Người bán không bàn giao giấy tờ";
            case MECHANICAL_FAILURE:
                return "Lỗi động cơ/kỹ thuật nghiêm trọng";
            case DELIVERY_FAILED:
                return "Giao hàng thất bại";
            default:
                return "Khác";
        }
    }
}
