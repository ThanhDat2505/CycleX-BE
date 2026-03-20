package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.DisputeReasonCode;
import com.example.cyclexbe.domain.enums.DisputeStatus;
import com.example.cyclexbe.domain.enums.NotificationType;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.*;
import com.example.cyclexbe.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class DisputeService {

    private static final Logger log = LoggerFactory.getLogger(DisputeService.class);

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
     * Flow: Check order → Check COMPLETED → Check 24h → Auto-assign Inspector
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

        // Check 24h time window from completion
        if (pr.getUpdatedAt() != null) {
            long hoursSinceCompleted = ChronoUnit.HOURS.between(pr.getUpdatedAt(), LocalDateTime.now());
            if (hoursSinceCompleted > 24) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Đã hết thời hạn khiếu nại (24h kể từ khi hoàn thành đơn hàng)");
            }
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

        // Auto-assign to Inspector (least-load strategy)
        User assignedInspector = autoAssignInspector();
        if (assignedInspector != null) {
            dispute.setAssignee(assignedInspector);
            log.info("Dispute auto-assigned to inspector {} (ID: {})",
                    assignedInspector.getFullName(), assignedInspector.getUserId());
        }

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

        // Notify assigned inspector
        if (assignedInspector != null) {
            notificationService.createNotification(
                    assignedInspector,
                    "Khiếu nại mới được giao",
                    "Bạn được giao xử lý khiếu nại #" + saved.getDisputeId() + " cho đơn hàng #" + pr.getRequestId(),
                    NotificationType.SYSTEM,
                    "DISPUTE",
                    saved.getDisputeId(),
                    "/inspector/disputes/" + saved.getDisputeId());
        }

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

    /**
     * Claim/accept a dispute (inspector picks it up → IN_PROGRESS)
     */
    @Transactional
    public DisputeDetailResponse claimDispute(Integer disputeId, Integer inspectorId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khiếu nại không tồn tại"));

        if (dispute.getStatus() != DisputeStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể nhận xử lý khiếu nại ở trạng thái OPEN");
        }

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector không tồn tại"));

        dispute.setAssignee(inspector);
        dispute.setStatus(DisputeStatus.IN_PROGRESS);
        Dispute saved = disputeRepository.save(dispute);

        log.info("Dispute #{} claimed by inspector {} (ID: {})", disputeId, inspector.getFullName(), inspectorId);

        // Notify buyer that dispute is being reviewed
        notificationService.createNotification(
                dispute.getRequester(),
                "Khiếu nại đang được xử lý",
                "Khiếu nại #" + disputeId + " đang được kiểm duyệt viên xem xét.",
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        return buildDetailResponse(saved);
    }

    /**
     * Escalate a dispute to Admin (when inspector cannot resolve)
     * Flow: Inspector cannot handle → assignedTo = ADMIN
     */
    @Transactional
    public DisputeDetailResponse escalateDispute(Integer disputeId, String escalationNote) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khiếu nại không tồn tại"));

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khiếu nại đã được xử lý, không thể chuyển tiếp");
        }

        // Find an admin to assign to
        List<User> admins = userRepository.findByRoleAndStatus(Role.ADMIN, "ACTIVE");
        if (admins.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy admin hoạt động");
        }
        User admin = admins.get(0);

        User previousAssignee = dispute.getAssignee();
        dispute.setAssignee(admin);
        dispute.setStatus(DisputeStatus.IN_PROGRESS);

        // Append escalation note to resolution note
        String existingNote = dispute.getResolutionNote() != null ? dispute.getResolutionNote() + "\n" : "";
        dispute.setResolutionNote(existingNote + "[Escalated] " + (escalationNote != null ? escalationNote : "Chuyển tiếp lên Admin"));

        Dispute saved = disputeRepository.save(dispute);

        log.info("Dispute #{} escalated to admin {} (ID: {})", disputeId, admin.getFullName(), admin.getUserId());

        // Notify admin about escalation
        notificationService.createNotification(
                admin,
                "Khiếu nại cần xử lý",
                "Khiếu nại #" + disputeId + " được chuyển tiếp từ kiểm duyệt viên. " +
                        (escalationNote != null ? "Ghi chú: " + escalationNote : ""),
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        // Notify buyer about escalation
        notificationService.createNotification(
                dispute.getRequester(),
                "Khiếu nại được chuyển tiếp",
                "Khiếu nại #" + disputeId + " đã được chuyển lên quản trị viên để xem xét.",
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        return buildDetailResponse(saved);
    }

    /**
     * Admin override / final decision (S-83)
     * Actions: BUYER_WIN → buyer thắng, SELLER_WIN → seller thắng, SPLIT → chia
     */
    @Transactional
    public DisputeDetailResponse adminOverride(Integer disputeId, AdminOverrideRequest req) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khiếu nại không tồn tại"));

        // Validate action
        String action = req.action;
        if (!"BUYER_WIN".equals(action) && !"SELLER_WIN".equals(action) && !"SPLIT".equals(action)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hành động không hợp lệ: " + action);
        }

        // Map to resolution
        DisputeStatus newStatus;
        String resolutionAction;
        if ("BUYER_WIN".equals(action)) {
            newStatus = DisputeStatus.RESOLVED;
            resolutionAction = "REFUND_BUYER";
        } else if ("SELLER_WIN".equals(action)) {
            newStatus = DisputeStatus.REJECTED;
            resolutionAction = "RELEASE_FUND_SELLER";
        } else { // SPLIT
            newStatus = DisputeStatus.RESOLVED;
            resolutionAction = "SPLIT";
        }

        dispute.setStatus(newStatus);
        dispute.setResolutionAction(resolutionAction);
        dispute.setResolutionNote(req.reason);
        dispute.setResolvedAt(LocalDateTime.now());

        // Set admin as assignee
        try {
            String authUserId = com.example.cyclexbe.security.SecurityUtils.getAuthenticatedUserId();
            userRepository.findById(Integer.parseInt(authUserId))
                    .ifPresent(dispute::setAssignee);
        } catch (Exception ignored) {
        }

        Dispute saved = disputeRepository.save(dispute);

        log.info("Dispute #{} overridden by admin. Action: {}", disputeId, action);

        // Notify buyer
        String resultMessage;
        if ("BUYER_WIN".equals(action)) {
            resultMessage = "Quản trị viên đã quyết định hoàn tiền cho bạn.";
        } else if ("SELLER_WIN".equals(action)) {
            resultMessage = "Quản trị viên đã quyết định từ chối khiếu nại.";
        } else {
            resultMessage = "Quản trị viên đã quyết định xử lý chia đều.";
        }

        notificationService.createNotification(
                dispute.getRequester(),
                "Kết quả khiếu nại (Admin)",
                "Khiếu nại #" + disputeId + ": " + resultMessage + " Ghi chú: " + req.reason,
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        // Notify seller
        notificationService.createNotification(
                dispute.getSeller(),
                "Kết quả khiếu nại (Admin)",
                "Khiếu nại #" + disputeId + ": " + resultMessage,
                NotificationType.SYSTEM,
                "DISPUTE",
                disputeId,
                "/disputes/" + disputeId);

        return buildDetailResponse(saved);
    }

    /**
     * Get disputes assigned to a specific inspector
     */
    @Transactional(readOnly = true)
    public Page<DisputeListRowResponse> getDisputesByAssignee(Integer assigneeId, String status, String search,
            String sortBy, String sortDir, int page, int pageSize) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy != null ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortField));

        DisputeStatus statusEnum = null;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                statusEnum = DisputeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        Page<Dispute> disputes = disputeRepository.findByAssigneeAndFilters(assigneeId, statusEnum, search, pageable);
        return disputes.map(DisputeListRowResponse::from);
    }

    // --- Helper methods ---

    /**
     * Auto-assign inspector using least-load strategy
     * Pick the ACTIVE inspector with fewest OPEN + IN_PROGRESS disputes
     */
    private User autoAssignInspector() {
        List<User> inspectors = userRepository.findByRoleAndStatus(Role.INSPECTOR, "ACTIVE");
        if (inspectors.isEmpty()) {
            log.warn("No active inspectors available for dispute assignment");
            return null;
        }

        List<DisputeStatus> activeStatuses = List.of(DisputeStatus.OPEN, DisputeStatus.IN_PROGRESS);
        return inspectors.stream()
                .min(Comparator.comparingLong(inspector ->
                        disputeRepository.countByAssigneeAndStatusIn(inspector, activeStatuses)))
                .orElse(inspectors.get(0));
    }

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
