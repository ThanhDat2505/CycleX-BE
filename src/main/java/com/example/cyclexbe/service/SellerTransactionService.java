package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.Delivery;
import com.example.cyclexbe.entity.Order;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.DeliveryRepository;
import com.example.cyclexbe.repository.OrderRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerTransactionService {

    private static final Logger log = LoggerFactory.getLogger(SellerTransactionService.class);

    private final OrderRepository orderRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    public SellerTransactionService(OrderRepository orderRepository,
            PurchaseRequestRepository purchaseRequestRepository,
            ProductRepository productRepository,
            DeliveryRepository deliveryRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.productRepository = productRepository;
        this.deliveryRepository = deliveryRepository;
        this.userRepository = userRepository;
    }

    // ==============================
    // S-52: Get Pending Transactions (now queries Orders)
    // ==============================
    public SellerPendingTransactionsResponse getPendingTransactions(
            Authentication authentication,
            int page,
            int size,
            String sortBy,
            String sortDir,
            TransactionType transactionType,
            String keyword) {

        Integer sellerId = parseCurrentUserId(authentication);

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }

        if (!sortBy.equals("createdAt") && !sortBy.equals("orderId")) {
            sortBy = "createdAt";
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "desc";
        }

        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "desc";
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Order> pageResult = orderRepository.findPendingOrdersForSeller(
                sellerId,
                transactionType,
                keyword,
                pageable);

        List<PendingTransactionListItemResponse> items = pageResult.getContent()
                .stream()
                .map(this::mapToPendingTransactionListItem)
                .collect(Collectors.toList());

        SellerPendingTransactionsResponse response = new SellerPendingTransactionsResponse();

        response.setContent(items);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setSortBy(sortBy);
        response.setSortDir(sortDir);

        SellerPendingTransactionsResponse.AppliedFilters filters = new SellerPendingTransactionsResponse.AppliedFilters(
                "PENDING_SELLER_CONFIRM",
                transactionType != null ? transactionType.toString() : null);

        response.setAppliedFilters(filters);

        return response;
    }

    // ==============================
    // S-53: Transaction Detail (now looks up Order by orderId)
    // ==============================
    public SellerTransactionDetailResponse getTransactionDetail(
            Authentication authentication,
            Integer orderId) {

        Integer sellerId = parseCurrentUserId(authentication);

        Order order = orderRepository.findByOrderIdAndSeller_UserId(orderId, sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found or you don't have permission"));

        return mapToTransactionDetail(order);
    }

    // ==============================
    // Confirm Transaction (updates Order status)
    // ==============================
    @Transactional
    public ActionTransactionResponse confirmTransaction(
            Authentication authentication,
            Integer orderId,
            ConfirmTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        Order order = orderRepository.findByOrderIdAndSeller_UserId(orderId, sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found or you don't have permission"));

        if (!order.getStatus().equals(OrderStatus.PENDING_SELLER_CONFIRM)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction cannot be confirmed in current status");
        }

        // Update Order status
        String sellerNote = (request != null && request.getNote() != null) ? request.getNote() : null;
        order.setStatus(OrderStatus.PENDING_DELIVERY);
        if (sellerNote != null) {
            order.setSellerNote(sellerNote);
        }
        Order updatedOrder = orderRepository.save(order);

        // Also update PurchaseRequest status for backward compatibility
        PurchaseRequest pr = order.getPurchaseRequest();
        if (pr != null) {
            pr.setStatus(PurchaseRequestStatus.SELLER_CONFIRMED);
            purchaseRequestRepository.save(pr);
        }

        // Auto-create delivery and assign a shipper
        createDeliveryForOrder(updatedOrder);

        ActionTransactionResponse response = new ActionTransactionResponse();
        response.setRequestId(updatedOrder.getOrderId());
        response.setStatus(updatedOrder.getStatus().name());
        response.setDisplayStatus(getOrderDisplayStatus(updatedOrder.getStatus()));
        response.setMessage("Transaction confirmed successfully");
        response.setUpdatedAt(updatedOrder.getUpdatedAt());
        return response;
    }

    /**
     * Auto-create a Delivery when seller confirms order.
     */
    private void createDeliveryForOrder(Order order) {
        List<User> shippers = userRepository.findByRoleAndStatus(Role.SHIPPER, "ACTIVE");
        if (shippers.isEmpty()) {
            log.warn("No active shippers available for order ID: {}", order.getOrderId());
            return;
        }

        User leastBusyShipper = shippers.stream()
                .min(Comparator
                        .comparingLong(s -> deliveryRepository.countByShipperAndStatus(s.getUserId(), "ASSIGNED")))
                .orElse(shippers.get(0));

        log.info("Auto-assigning delivery for order ID {} to shipper {} (ID: {})",
                order.getOrderId(),
                leastBusyShipper.getFullName(),
                leastBusyShipper.getUserId());

        Delivery delivery = new Delivery();
        delivery.setShipper(leastBusyShipper);
        delivery.setTransaction(order.getPurchaseRequest());
        delivery.setOrder(order);
        if (order.getProduct() != null && order.getProduct().getListing() != null) {
            delivery.setListing(order.getProduct().getListing());
            String pickupAddr = order.getProduct().getListing().getPickupAddress();
            delivery.setPickupAddress(pickupAddr != null ? pickupAddr
                    : order.getProduct().getListing().getLocationCity());
        }
        delivery.setStatus("ASSIGNED");

        String dropoffAddr = order.getBuyerNote();
        delivery.setDropoffAddress(dropoffAddr != null && !dropoffAddr.isBlank()
                ? dropoffAddr
                : "Địa chỉ người mua");

        deliveryRepository.save(delivery);

        log.info("Delivery created (ID: {}) for order ID {} assigned to shipper ID {}",
                delivery.getDeliveryId(), order.getOrderId(), leastBusyShipper.getUserId());
    }

    // ==============================
    // Reject Transaction (sets Order CANCELLED + Product AVAILABLE)
    // ==============================
    @Transactional
    public ActionTransactionResponse rejectTransaction(
            Authentication authentication,
            Integer orderId,
            RejectTransactionRequest request) {

        Integer sellerId = parseCurrentUserId(authentication);

        if (request == null || request.getReason() == null
                || request.getReason().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reason is required");
        }

        Order order = orderRepository.findByOrderIdAndSeller_UserId(orderId, sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found or you don't have permission"));

        if (!order.getStatus().equals(OrderStatus.PENDING_SELLER_CONFIRM)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transaction cannot be rejected in current status");
        }

        // Update Order status
        order.setStatus(OrderStatus.CANCELLED);
        order.setSellerNote("REJECTED: " + request.getReason());
        orderRepository.save(order);

        // Update PurchaseRequest for backward compat
        PurchaseRequest pr = order.getPurchaseRequest();
        if (pr != null) {
            pr.setStatus(PurchaseRequestStatus.CANCELLED);
            pr.setNote("REJECTED by seller: " + request.getReason());
            purchaseRequestRepository.save(pr);
        }

        // Set product back to AVAILABLE so other buyers can order
        if (order.getProduct() != null) {
            order.getProduct().setStatus("AVAILABLE");
            productRepository.save(order.getProduct());
        }

        ActionTransactionResponse response = new ActionTransactionResponse();
        response.setRequestId(order.getOrderId());
        response.setStatus(OrderStatus.CANCELLED.name());
        response.setDisplayStatus("Đã từ chối");
        response.setMessage("Transaction rejected successfully");
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    // ==============================
    // Helper Methods
    // ==============================

    private Integer parseCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No authentication found");
        }
        try {
            return Integer.parseInt(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid user ID in token");
        }
    }

    private PendingTransactionListItemResponse mapToPendingTransactionListItem(Order order) {
        PendingTransactionListItemResponse response = new PendingTransactionListItemResponse();
        response.setOrderId(order.getOrderId());
        response.setRequestId(order.getOrderId());
        response.setBuyerName(order.getBuyer().getFullName());
        response.setListingTitle(order.getProduct() != null
                ? order.getProduct().getName()
                : "");
        response.setTransactionType(order.getTransactionType() != null
                ? order.getTransactionType().toString()
                : "");
        response.setProductPrice(order.getProduct() != null
                ? order.getProduct().getPrice()
                : order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus(order.getStatus().name());
        response.setDisplayStatus(getOrderDisplayStatus(order.getStatus()));
        return response;
    }

    private SellerTransactionDetailResponse mapToTransactionDetail(Order order) {
        SellerTransactionDetailResponse response = new SellerTransactionDetailResponse();
        response.setRequestId(order.getOrderId());
        response.setBuyerName(order.getBuyer().getFullName());
        response.setBuyerEmail(order.getBuyer().getEmail());
        response.setBuyerPhone(order.getBuyer().getPhone());
        if (order.getProduct() != null) {
            response.setListingTitle(order.getProduct().getName());
            response.setProductPrice(order.getProduct().getPrice());
            if (order.getProduct().getListing() != null) {
                response.setListingId(order.getProduct().getListing().getListingId());
            }
        }
        response.setTransactionType(order.getTransactionType() != null
                ? order.getTransactionType().toString()
                : "");
        response.setDepositAmount(order.getDepositAmount());
        response.setPlatformFee(order.getPlatformFee());
        response.setInspectionFee(order.getInspectionFee());
        response.setStatus(order.getStatus().name());
        response.setDisplayStatus(getOrderDisplayStatus(order.getStatus()));
        response.setNote(order.getBuyerNote());
        response.setDesiredTransactionTime(order.getDesiredTransactionTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    private String getOrderDisplayStatus(OrderStatus status) {
        return switch (status) {
            case PENDING_SELLER_CONFIRM -> "Chờ xác nhận";
            case PENDING_DELIVERY -> "Chờ giao hàng";
            case IN_DELIVERY -> "Đang giao hàng";
            case DELIVERED -> "Đã giao hàng";
            case COMPLETED -> "Hoàn tất";
            case CANCELLED -> "Hủy bỏ";
            case DISPUTED -> "Tranh chấp";
        };
    }
}