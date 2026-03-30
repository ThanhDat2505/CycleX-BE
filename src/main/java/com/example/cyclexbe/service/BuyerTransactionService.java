package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.dto.BuyerCancelTransactionResponse;
import com.example.cyclexbe.dto.BuyerTransactionActionsDto;
import com.example.cyclexbe.dto.BuyerTransactionDetailResponse;
import com.example.cyclexbe.dto.BuyerTransactionListItemResponse;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.Order;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.exception.ForbiddenException;
import com.example.cyclexbe.exception.InvalidListingException;
import com.example.cyclexbe.exception.PurchaseRequestException;
import com.example.cyclexbe.repository.ListingImageRepository;
import com.example.cyclexbe.repository.OrderRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.DisputeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for S-54: Buyer Transaction Detail
 * Handles buyer-specific transaction operations like viewing details and
 * cancellation.
 * Now uses Order entity directly (PurchaseRequest removed from flow).
 */
@Service
@Transactional
public class BuyerTransactionService {

        private final ProductRepository productRepository;
        private final ListingImageRepository listingImageRepository;
        private final OrderRepository orderRepository;
        private final DisputeRepository disputeRepository;

        public BuyerTransactionService(
                        ProductRepository productRepository,
                        ListingImageRepository listingImageRepository,
                        OrderRepository orderRepository,
                        DisputeRepository disputeRepository) {
                this.productRepository = productRepository;
                this.listingImageRepository = listingImageRepository;
                this.orderRepository = orderRepository;
                this.disputeRepository = disputeRepository;
        }

        @Transactional(readOnly = true)
        public List<BuyerTransactionListItemResponse> getBuyerTransactions(Integer buyerId) {
                return orderRepository.findByBuyer_UserIdOrderByCreatedAtDesc(buyerId)
                                .stream()
                                .map(order -> mapToBuyerTransactionItem(order, buyerId))
                                .toList();
        }

        /**
         * F1: Load transaction detail for buyer
         * GET /api/buyer/transactions/{id}
         *
         * @param orderId The order ID
         * @param buyerId The buyer ID from authentication
         * @return Transaction detail with seller info, listing info, and available
         *         actions
         * @throws PurchaseRequestException if order not found (404)
         * @throws ForbiddenException       if buyer mismatch (403)
         */
        @Transactional(readOnly = true)
        public BuyerTransactionDetailResponse getTransactionDetail(Integer orderId, Integer buyerId) {
                // First check if order exists at all
                Optional<Order> optionalOrder = orderRepository.findById(orderId);

                if (optionalOrder.isEmpty()) {
                        throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                                        "Không tìm thấy giao dịch");
                }

                Order order = optionalOrder.get();

                // Check if buyer matches
                if (!order.getBuyer().getUserId().equals(buyerId)) {
                        throw new ForbiddenException("FORBIDDEN_BUYER_MISMATCH",
                                        "Bạn không có quyền xem giao dịch này");
                }

                // Fetch with eager loading
                Optional<Order> optionalOrderWithEager = orderRepository
                                .findByOrderIdAndBuyerIdWithEager(orderId, buyerId);
                if (optionalOrderWithEager.isEmpty()) {
                        throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                                        "Không tìm thấy giao dịch");
                }

                order = optionalOrderWithEager.get();
                Product product = order.getProduct();
                User seller = product.getSeller();

                if (seller == null) {
                        throw new InvalidListingException("SELLER_NOT_FOUND", "Không tìm thấy người bán của sản phẩm");
                }

                // Determine if buyer can cancel
                BuyerTransactionActionsDto actions = determineActions(order);

                // Build seller info
                BuyerTransactionDetailResponse.SellerInfoDto sellerInfo = new BuyerTransactionDetailResponse.SellerInfoDto(
                                seller.getUserId(),
                                seller.getFullName(),
                                seller.getPhone(),
                                seller.getAvatarUrl());

                // Build listing info from product
                BuyerTransactionDetailResponse.ListingInfoDto productInfo = new BuyerTransactionDetailResponse.ListingInfoDto(
                                product.getListing().getListingId(),
                                product.getName(),
                                product.getDescription(),
                                product.getListing().getBikeType(),
                                product.getListing().getBrand(),
                                product.getListing().getModel(),
                                product.getListing().getManufactureYear(),
                                product.getListing().getCondition(),
                                product.getListing().getStatus(),
                                product.getListing().getPickupAddress(),
                                product.getListing().getLocationCity());

                // Build timeline
                BuyerTransactionDetailResponse.TimelineDto timeline = new BuyerTransactionDetailResponse.TimelineDto(
                                order.getCreatedAt(),
                                order.getUpdatedAt());

                // Build response using orderId
                BuyerTransactionDetailResponse response = new BuyerTransactionDetailResponse(
                                order.getOrderId(),
                                order.getStatus(),
                                sellerInfo,
                                productInfo,
                                product.getPrice(),
                                order.getDepositAmount(),
                                order.getPlatformFee(),
                                order.getInspectionFee(),
                                order.getBuyerNote(),
                                order.getDesiredTransactionTime(),
                                timeline,
                                actions,
                                order.getCreatedAt(),
                                order.getUpdatedAt());

                response.setOrderStatus(order.getStatus().name());

                return response;
        }

        /**
         * F2: Cancel transaction
         * POST /api/buyer/transactions/{id}/cancel
         *
         * Only allowed when order.status == PENDING_SELLER_CONFIRM
         * When cancelled:
         * - order.status -> CANCELLED
         * - product.status -> AVAILABLE
         *
         * @param orderId The order ID
         * @param buyerId The buyer ID from authentication
         * @return Cancel transaction response with status updates and redirect URL
         * @throws PurchaseRequestException if order not found (404), or invalid
         *                                  status (409)
         * @throws ForbiddenException       if buyer mismatch (403)
         */
        public BuyerCancelTransactionResponse cancelTransaction(Integer orderId, Integer buyerId) {
                Optional<Order> optionalOrder = orderRepository.findById(orderId);

                if (optionalOrder.isEmpty()) {
                        throw new PurchaseRequestException("TRANSACTION_NOT_FOUND",
                                        "Không tìm thấy giao dịch");
                }

                Order order = optionalOrder.get();

                // Check if buyer matches
                if (!order.getBuyer().getUserId().equals(buyerId)) {
                        throw new ForbiddenException("FORBIDDEN_BUYER_MISMATCH",
                                        "Bạn không có quyền hủy giao dịch này");
                }

                // Validate order status
                if (order.getStatus() != OrderStatus.PENDING_SELLER_CONFIRM) {
                        throw new PurchaseRequestException("INVALID_TRANSACTION_STATUS",
                                        "Transaction can only be cancelled when status is PENDING_SELLER_CONFIRM. " +
                                                        "Current status: " + order.getStatus());
                }

                OrderStatus oldStatus = order.getStatus();

                // Update order status
                order.setStatus(OrderStatus.CANCELLED);
                Order savedOrder = orderRepository.save(order);

                // Update product status -> AVAILABLE
                Product product = order.getProduct();
                product.setStatus("AVAILABLE");
                productRepository.save(product);

                // Build and return response
                return new BuyerCancelTransactionResponse(
                                savedOrder.getOrderId(),
                                oldStatus,
                                savedOrder.getStatus(),
                                "/buyer/transactions");
        }

        /**
         * Determine available actions for buyer on this order.
         */
        private BuyerTransactionActionsDto determineActions(Order order) {
                boolean canCancel = order.getStatus() == OrderStatus.PENDING_SELLER_CONFIRM;
                String cancelDisabledReason = null;

                if (!canCancel) {
                        cancelDisabledReason = "Transaction can only be cancelled when status is PENDING_SELLER_CONFIRM. "
                                        +
                                        "Current status: " + order.getStatus();
                }

                return new BuyerTransactionActionsDto(canCancel, cancelDisabledReason);
        }

        private BuyerTransactionListItemResponse mapToBuyerTransactionItem(Order order, Integer buyerId) {
                Product product = order.getProduct();
                Integer listingId = product != null && product.getListing() != null
                                ? product.getListing().getListingId()
                                : null;

                String listingImage = null;
                if (product != null && product.getListing() != null) {
                        listingImage = listingImageRepository.findByBikeListingOrderByImageOrder(product.getListing())
                                        .stream()
                                        .map(img -> img.getImagePath())
                                        .filter(path -> path != null && !path.isBlank())
                                        .findFirst()
                                        .orElse(null);
                }

                BigDecimal totalAmount = safeAmount(order.getTotalAmount());

                // Check if this order has a dispute via PurchaseRequest
                boolean hasDispute = false;
                if (order.getPurchaseRequest() != null && order.getPurchaseRequest().getRequestId() != null) {
                        hasDispute = disputeRepository.existsByPurchaseRequest_RequestId(
                                        order.getPurchaseRequest().getRequestId());
                }

                return new BuyerTransactionListItemResponse(
                                order.getOrderId(),
                                buyerId,
                                product != null && product.getSeller() != null ? product.getSeller().getUserId() : null,
                                listingId,
                                product != null ? product.getName() : null,
                                listingImage,
                                product != null && product.getSeller() != null ? product.getSeller().getFullName()
                                                : null,
                                product != null && product.getSeller() != null ? product.getSeller().getPhone() : null,
                                order.getTransactionType() != null ? order.getTransactionType().name() : null,
                                order.getStatus().name(),
                                totalAmount,
                                order.getCreatedAt(),
                                hasDispute);
        }

        private BigDecimal safeAmount(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }
}
