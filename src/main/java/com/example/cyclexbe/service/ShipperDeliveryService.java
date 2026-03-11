package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.NotificationType;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.Delivery;
import com.example.cyclexbe.entity.ListingImage;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.DeliveryRepository;
import com.example.cyclexbe.repository.ListingImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service for Shipper Delivery Management
 * Handles retrieval of assigned deliveries for shippers (S-60 and S-61)
 */
@Service
@Transactional
public class ShipperDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;
    private final ListingImageRepository listingImageRepository;

    public ShipperDeliveryService(DeliveryRepository deliveryRepository,
                                  NotificationService notificationService,
                                  ListingImageRepository listingImageRepository) {
        this.deliveryRepository = deliveryRepository;
        this.notificationService = notificationService;
        this.listingImageRepository = listingImageRepository;
    }

    /**
     * Get assigned deliveries for shipper with pagination (S-60 F2)
     * Returns only deliveries with status = ASSIGNED
     */
    @Transactional(readOnly = true)
    public ShipperAssignedDeliveryListResponse getAssignedDeliveries(Integer shipperId, Pageable pageable) {
        Page<Delivery> deliveriesPage =
                deliveryRepository.findByShipper_UserIdAndStatusAndTransaction_Status(
                        shipperId,
                        "ASSIGNED",
                        PurchaseRequestStatus.SELLER_CONFIRMED,
                        pageable
                );

        List<ShipperAssignedDeliveryItemDto> items = deliveriesPage.getContent()
                .stream()
                .map(this::mapToAssignedItemDto)
                .toList();

        return new ShipperAssignedDeliveryListResponse(
                items,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                deliveriesPage.getTotalElements(),
                deliveriesPage.getTotalPages()
        );
    }

    /**
     * Map Delivery entity to assigned delivery item DTO (S-60)
     */
    private ShipperAssignedDeliveryItemDto mapToAssignedItemDto(Delivery delivery) {
        return new ShipperAssignedDeliveryItemDto(
                delivery.getTransaction().getRequestId(),
                delivery.getStatus(),
                delivery.getListing().getListingId(),
                delivery.getListing().getTitle(),
                delivery.getListing().getSeller().getUserId(),
                delivery.getListing().getSeller().getFullName(),
                delivery.getUpdatedAt()
        );
    }

    /**
     * Get deliveries for shipper with status filtering and pagination (S-61 F1/F2)
     * Supports filtering by single or multiple statuses: ASSIGNED, IN_PROGRESS, FAILED
     * Validates status parameter and returns 400 if invalid
     *
     * @param shipperId Shipper ID extracted from authentication
     * @param status Comma-separated or single status filter (optional, null = all statuses)
     * @param pageable Pagination and sorting parameters
     * @return Paginated list of deliveries matching filters
     * @throws ResponseStatusException 400 if status is invalid
     */
    @Transactional(readOnly = true)
    public ShipperDeliveryListResponse getDeliveriesByStatus(
            Integer shipperId,
            String status,
            Pageable pageable) {

        // Valid statuses for shipper delivery operations
        List<String> validStatuses = Arrays.asList("ASSIGNED", "IN_PROGRESS", "FAILED");
        List<String> statusesToFilter;

        if (status == null || status.trim().isEmpty()) {
            // If no status filter, get all valid statuses
            statusesToFilter = validStatuses;
        } else {
            // Parse comma-separated or single status
            statusesToFilter = Arrays.stream(status.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .toList();

            // Validate all provided statuses
            for (String s : statusesToFilter) {
                if (!validStatuses.contains(s)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid status: " + s + ". Valid values are: ASSIGNED, IN_PROGRESS, FAILED"
                    );
                }
            }
        }

        // Fetch deliveries with status filter and pagination
        Page<Delivery> deliveriesPage = deliveryRepository.findByShipper_UserIdAndStatusIn(
                shipperId,
                statusesToFilter,
                pageable
        );

        // Map entities to DTOs
        List<ShipperDeliveryListItemDto> items = deliveriesPage.getContent()
                .stream()
                .map(this::mapToDeliveryListItemDto)
                .toList();

        return new ShipperDeliveryListResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                deliveriesPage.getTotalElements(),
                deliveriesPage.getTotalPages(),
                items
        );
    }

    /**
     * Map Delivery entity to delivery list item DTO (S-61 F1/F2)
     */
    private ShipperDeliveryListItemDto mapToDeliveryListItemDto(Delivery delivery) {
        String pickupCity = extractCityFromAddress(delivery.getPickupAddress());
        String deliveryCity = extractCityFromAddress(delivery.getDropoffAddress());

        // Get product image (first image of listing)
        String productImage = null;
        try {
            List<ListingImage> images = listingImageRepository
                    .findByBikeListingOrderByImageOrder(delivery.getListing());
            if (!images.isEmpty()) {
                productImage = images.get(0).getImagePath();
            }
        } catch (Exception e) {
            // Ignore image fetch errors
        }

        // Get buyer info from transaction
        User buyer = delivery.getTransaction().getBuyer();
        // Get seller info from listing
        User seller = delivery.getListing().getSeller();

        return new ShipperDeliveryListItemDto(
                delivery.getDeliveryId(),
                delivery.getTransaction().getRequestId(),
                pickupCity,
                deliveryCity,
                delivery.getPickupAddress(),
                delivery.getDropoffAddress(),
                delivery.getStatus(),
                delivery.getListing().getTitle(),
                productImage,
                seller != null ? seller.getFullName() : null,
                seller != null ? seller.getPhone() : null,
                buyer != null ? buyer.getFullName() : null,
                buyer != null ? buyer.getPhone() : null,
                delivery.getCreatedAt(),
                delivery.getCreatedAt()
        );
    }

    /**
     * Extract city from address string
     * Placeholder implementation - parse from address or use related entity
     */
    private String extractCityFromAddress(String address) {
        if (address == null || address.isEmpty()) {
            return "Unknown";
        }
        // Simple extraction: last part after comma
        String[] parts = address.split(",");
        return parts.length > 0 ? parts[parts.length - 1].trim() : "Unknown";
    }

    /**
     * Get delivery detail with full information, timeline, and actions (S-61 F3/F4/F7/F8)
     * Security: Validates that the delivery belongs to the current shipper (throws 403 if not)
     *
     * @param deliveryId Delivery ID from path parameter
     * @param shipperId Shipper ID extracted from authentication
     * @return Full delivery detail response with timeline and available actions
     * @throws ResponseStatusException 403 if delivery doesn't belong to shipper
     * @throws ResponseStatusException 404 if delivery not found
     */
    @Transactional(readOnly = true)
    public ShipperDeliveryDetailResponse getDeliveryDetail(Integer deliveryId, Integer shipperId) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        ShipperContactInfoDto seller = new ShipperContactInfoDto(
                delivery.getListing().getSeller().getUserId(),
                delivery.getListing().getSeller().getFullName(),
                delivery.getListing().getSeller().getPhone(),
                delivery.getPickupAddress(),
                extractCityFromAddress(delivery.getPickupAddress())
        );

        ShipperContactInfoDto buyer = new ShipperContactInfoDto(
                delivery.getTransaction().getBuyer().getUserId(),
                delivery.getTransaction().getBuyer().getFullName(),
                delivery.getTransaction().getBuyer().getPhone(),
                delivery.getDropoffAddress(),
                extractCityFromAddress(delivery.getDropoffAddress())
        );

        ShipperPickupLocationDto pickup = new ShipperPickupLocationDto(
                delivery.getPickupAddress(),
                extractCityFromAddress(delivery.getPickupAddress()),
                delivery.getListing().getSeller().getFullName(),
                delivery.getListing().getSeller().getPhone()
        );

        ShipperDeliveryLocationDto dropoff = new ShipperDeliveryLocationDto(
                delivery.getDropoffAddress(),
                extractCityFromAddress(delivery.getDropoffAddress()),
                delivery.getTransaction().getBuyer().getFullName(),
                delivery.getTransaction().getBuyer().getPhone()
        );

        ShipperDeliveryTimelineDto timeline = buildTimeline(delivery);
        ShipperDeliveryActionsDto actions = buildActions(delivery); // <-- sửa theo đề

        return new ShipperDeliveryDetailResponse(
                delivery.getDeliveryId(),
                delivery.getTransaction().getRequestId(),
                delivery.getStatus(),
                seller,
                buyer,
                pickup,
                dropoff,
                timeline,
                actions
        );
    }

    /**
     * Build timeline DTO based on delivery status
     * Represents status changes over time
     */
    private ShipperDeliveryTimelineDto buildTimeline(Delivery delivery) {
        LocalDateTime assignedTime = delivery.getCreatedAt();
        LocalDateTime shippedTime = null;
        LocalDateTime expectedDeliveryTime = null;
        LocalDateTime completedTime = null;

        String status = delivery.getStatus() == null ? "" : delivery.getStatus().trim().toUpperCase();

        if ("IN_PROGRESS".equals(status) || "FAILED".equals(status) || "COMPLETED".equals(status)) {
            shippedTime = delivery.getUpdatedAt();
        }
        if ("IN_PROGRESS".equals(status) && shippedTime != null) {
            expectedDeliveryTime = shippedTime.plusHours(24); // placeholder vì entity chưa có expectedAt
        }
        if ("COMPLETED".equals(status)) {
            completedTime = delivery.getUpdatedAt();
            if (expectedDeliveryTime == null) expectedDeliveryTime = delivery.getUpdatedAt();
        }

        return new ShipperDeliveryTimelineDto(assignedTime, shippedTime, expectedDeliveryTime, completedTime);
    }

    private ShipperDeliveryActionsDto buildActions(Delivery delivery) {
        String status = delivery.getStatus() == null ? "" : delivery.getStatus().trim().toUpperCase();

        boolean canStart = false;
        boolean canConfirm = false;
        boolean canReportFailed = false;
        String message = null;

        if ("ASSIGNED".equals(status)) {
            canStart = true;
            message = "Delivery is assigned. Press Start to begin delivery.";
        } else if ("IN_PROGRESS".equals(status)) {
            canConfirm = true;
            canReportFailed = true;
        } else if ("FAILED".equals(status)) {
            canReportFailed = true;
            message = "Delivery is marked as FAILED.";
        } else {
            message = "Delivery status changed. No actions available.";
        }

        return new ShipperDeliveryActionsDto(canStart, canConfirm, canReportFailed, message);
    }

    // ========== Start Delivery ==========

    /**
     * Start delivery (ASSIGNED → IN_PROGRESS)
     * POST /api/shipper/deliveries/{deliveryId}/start
     *
     * Rules:
     *  - Only the assigned shipper can start
     *  - delivery.status must be ASSIGNED, otherwise 409
     */
    @Transactional
    public ShipperStartDeliveryResponse startDelivery(Integer deliveryId, Integer shipperId) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        if (!"ASSIGNED".equals(delivery.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Delivery can only be started when status is ASSIGNED. Current status: " + delivery.getStatus()
            );
        }

        delivery.setStatus("IN_PROGRESS");
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        return new ShipperStartDeliveryResponse(
                delivery.getDeliveryId(),
                delivery.getStatus(),
                "Delivery started successfully",
                LocalDateTime.now()
        );
    }

    // ========== S-63: Delivery Confirmation ==========

    /**
     * Load delivery info for the confirmation screen (S-63 – Load Delivery Info)
     * GET /api/shipper/deliveries/{deliveryId}/confirmation
     *
     * @param deliveryId Delivery ID
     * @param shipperId  Authenticated shipper's user ID
     * @return Delivery info needed for the confirmation screen
     */
    @Transactional(readOnly = true)
    public ShipperDeliveryConfirmationResponse getDeliveryConfirmation(Integer deliveryId, Integer shipperId) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        boolean canConfirm = "IN_PROGRESS".equals(delivery.getStatus());

        return new ShipperDeliveryConfirmationResponse(
                delivery.getDeliveryId(),
                delivery.getTransaction().getRequestId(),
                delivery.getStatus(),
                delivery.getPickupAddress(),
                delivery.getDropoffAddress(),
                delivery.getTransaction().getBuyer().getFullName(),
                delivery.getTransaction().getBuyer().getPhone(),
                delivery.getListing().getTitle(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt(),
                canConfirm
        );
    }

    /**
     * Confirm delivery success (S-63 – Confirm Delivery)
     * POST /api/shipper/deliveries/{deliveryId}/confirm
     *
     * Rules:
     *  - delivery.status must be IN_PROGRESS, otherwise 409
     *  - On success: delivery→DELIVERED, transaction→COMPLETED, listing→SOLD
     *  - Double-submit is blocked by the same status check (409)
     *
     * @param deliveryId Delivery ID
     * @param shipperId  Authenticated shipper's user ID
     * @return Confirmation result
     */
    @Transactional
    public ShipperDeliveryConfirmResponse confirmDelivery(Integer deliveryId, Integer shipperId) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        // Status rule + prevent double submit
        if (!"IN_PROGRESS".equals(delivery.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Delivery cannot be confirmed in current status: " + delivery.getStatus()
            );
        }

        // 1. delivery.status → DELIVERED
        delivery.setStatus("DELIVERED");

        // 2. transaction.status → COMPLETED
        PurchaseRequest transaction = delivery.getTransaction();
        transaction.setStatus(PurchaseRequestStatus.COMPLETED);

        // 3. listing.status → SOLD
        BikeListing listing = delivery.getListing();
        listing.setStatus(BikeListingStatus.SOLD);

        // Persist (cascade via dirty-checking inside the same transaction)
        deliveryRepository.save(delivery);

        // 4. Send notifications to buyer and seller
        sendDeliverySuccessNotifications(delivery, transaction, listing);

        return new ShipperDeliveryConfirmResponse(
                delivery.getDeliveryId(),
                delivery.getStatus(),
                transaction.getStatus().name(),
                listing.getStatus().name(),
                "Delivery confirmed successfully",
                LocalDateTime.now()
        );
    }

    /**
     * Send DELIVERY_SUCCESS notifications to both buyer and seller
     */
    private void sendDeliverySuccessNotifications(Delivery delivery, PurchaseRequest transaction, BikeListing listing) {
        Integer requestId = transaction.getRequestId();
        String listingTitle = listing.getTitle();

        // Notify buyer
        User buyer = transaction.getBuyer();
        notificationService.createNotification(
                buyer,
                "Giao hàng thành công",
                "Đơn hàng #" + requestId + " (" + listingTitle + ") đã được giao thành công.",
                NotificationType.DELIVERY_SUCCESS,
                "TRANSACTION",
                requestId,
                "/buyer/transactions/" + requestId
        );

        // Notify seller
        User seller = listing.getSeller();
        notificationService.createNotification(
                seller,
                "Giao hàng thành công",
                "Đơn hàng #" + requestId + " (" + listingTitle + ") đã được giao thành công đến người mua.",
                NotificationType.DELIVERY_SUCCESS,
                "TRANSACTION",
                requestId,
                "/seller/transactions/" + requestId
        );
    }

    /**
     * Shared helper: find delivery belonging to shipper, or throw 403/404.
     */
    private Delivery findDeliveryOrThrow(Integer deliveryId, Integer shipperId) {
        Delivery delivery = deliveryRepository
                .findByDeliveryIdAndShipper_UserId(deliveryId, shipperId)
                .orElse(null);

        if (delivery == null) {
            if (deliveryRepository.existsByDeliveryId(deliveryId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You don't have permission to access this delivery");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found");
        }
        return delivery;
    }

    // ========== Failure Report ==========

    /**
     * Load delivery info for the failure report screen
     * GET /api/shipper/deliveries/{deliveryId}/failure-report
     *
     * Conditions: delivery must be IN_PROGRESS and assigned to current shipper
     */
    @Transactional(readOnly = true)
    public ShipperFailureReportInfoResponse getFailureReportInfo(Integer deliveryId, Integer shipperId) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        if (!"IN_PROGRESS".equals(delivery.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Can only report failure for deliveries with status IN_PROGRESS. Current status: " + delivery.getStatus()
            );
        }

        PurchaseRequest transaction = delivery.getTransaction();

        return new ShipperFailureReportInfoResponse(
                delivery.getDeliveryId(),
                transaction.getRequestId(),
                delivery.getListing().getListingId(),
                transaction.getBuyer().getFullName(),
                transaction.getBuyer().getPhone(),
                delivery.getListing().getSeller().getFullName(),
                delivery.getDropoffAddress(),
                delivery.getListing().getTitle(),
                delivery.getStatus(),
                transaction.getStatus().name()
        );
    }

    /**
     * Submit delivery failure report
     * POST /api/shipper/deliveries/{deliveryId}/failure-report
     *
     * Actions:
     *  - delivery.status → FAILED
     *  - transaction.status → DISPUTED
     *  - Save failureReason
     *  - Notify buyer and seller
     */
    @Transactional
    public ShipperFailureReportResponse submitFailureReport(Integer deliveryId, Integer shipperId, String reason) {

        Delivery delivery = findDeliveryOrThrow(deliveryId, shipperId);

        if (!"IN_PROGRESS".equals(delivery.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Can only report failure for deliveries with status IN_PROGRESS. Current status: " + delivery.getStatus()
            );
        }

        // 1. delivery.status → FAILED, save failureReason
        delivery.setStatus("FAILED");
        delivery.setFailureReason(reason);

        // 2. transaction.status → DISPUTED
        PurchaseRequest transaction = delivery.getTransaction();
        transaction.setStatus(PurchaseRequestStatus.DISPUTED);

        deliveryRepository.save(delivery);

        // 3. Send notifications to buyer and seller
        sendDeliveryFailedNotifications(delivery, transaction);

        return new ShipperFailureReportResponse(
                "Delivery failed report submitted successfully",
                delivery.getDeliveryId(),
                delivery.getStatus(),
                transaction.getStatus().name()
        );
    }

    /**
     * Send DELIVERY_FAILED notifications to both buyer and seller
     */
    private void sendDeliveryFailedNotifications(Delivery delivery, PurchaseRequest transaction) {
        Integer requestId = transaction.getRequestId();
        String listingTitle = delivery.getListing().getTitle();

        // Notify buyer
        User buyer = transaction.getBuyer();
        notificationService.createNotification(
                buyer,
                "Giao hàng thất bại",
                "Đơn hàng #" + requestId + " (" + listingTitle + ") giao hàng thất bại. Lý do: " + delivery.getFailureReason(),
                NotificationType.DELIVERY_FAILED,
                "TRANSACTION",
                requestId,
                "/buyer/transactions/" + requestId
        );

        // Notify seller
        User seller = delivery.getListing().getSeller();
        notificationService.createNotification(
                seller,
                "Giao hàng thất bại",
                "Đơn hàng #" + requestId + " (" + listingTitle + ") giao hàng thất bại. Lý do: " + delivery.getFailureReason(),
                NotificationType.DELIVERY_FAILED,
                "TRANSACTION",
                requestId,
                "/seller/transactions/" + requestId
        );
    }
}