package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.dto.OrderResponse;
import com.example.cyclexbe.dto.PurchaseRequestCreateRequest;
import com.example.cyclexbe.entity.Order;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.OrderRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.PurchaseRequestRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderService {

    private static final int DEPOSIT_RATE_PERCENT = 10;
    private static final BigDecimal DEFAULT_PLATFORM_FEE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_INSPECTION_FEE = BigDecimal.ZERO;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

    public OrderService(OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            PurchaseRequestRepository purchaseRequestRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    /**
     * Create an Order directly from a Product (new flow - replaces
     * PurchaseRequest).
     * Buyer clicks "Đặt hàng" → Order created with PENDING_SELLER_CONFIRM.
     * Product status set to RESERVED so other buyers cannot order.
     * A thin PurchaseRequest is also created internally for backward-compat with
     * Delivery FK.
     */
    @Transactional
    public Order createOrderFromProduct(Integer productId, Integer buyerId, PurchaseRequestCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buyer not found"));
        User seller = product.getSeller();

        if (seller == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product seller not found");
        }
        if (seller.getUserId().equals(buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot buy your own product");
        }
        if (!"AVAILABLE".equals(product.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Sản phẩm này đã có người đặt mua và đang chờ xác nhận. Vui lòng quay lại sau!");
        }
        if (request.getTransactionType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction type is required");
        }
        if (request.getDesiredTransactionTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Desired transaction time is required");
        }

        BigDecimal productPrice = product.getPrice();
        BigDecimal depositAmount = productPrice.multiply(BigDecimal.valueOf(DEPOSIT_RATE_PERCENT))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Create internal PurchaseRequest for Delivery FK compatibility
        PurchaseRequest pr = new PurchaseRequest();
        pr.setProduct(product);
        pr.setBuyer(buyer);
        pr.setTransactionType(request.getTransactionType());
        pr.setDesiredTransactionTime(request.getDesiredTransactionTime());
        pr.setNote(request.getNote());
        pr.setDepositAmount(depositAmount);
        pr.setPlatformFee(DEFAULT_PLATFORM_FEE);
        pr.setInspectionFee(DEFAULT_INSPECTION_FEE);
        pr.setStatus(PurchaseRequestStatus.PENDING_SELLER_CONFIRM);
        PurchaseRequest savedPr = purchaseRequestRepository.save(pr);

        // Create the Order
        Order order = new Order();
        order.setPurchaseRequest(savedPr);
        order.setProduct(product);
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setTotalAmount(productPrice);
        order.setDepositAmount(depositAmount);
        order.setPlatformFee(DEFAULT_PLATFORM_FEE);
        order.setInspectionFee(DEFAULT_INSPECTION_FEE);
        order.setTransactionType(request.getTransactionType());
        order.setDesiredTransactionTime(request.getDesiredTransactionTime());
        order.setBuyerNote(request.getNote());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(OrderStatus.PENDING_SELLER_CONFIRM);

        Order savedOrder = orderRepository.save(order);

        // Mark product as RESERVED so other buyers cannot order it
        product.setStatus("RESERVED");
        productRepository.save(product);

        return savedOrder;
    }

    /**
     * Legacy: Create an Order from a confirmed PurchaseRequest.
     * Kept for backward compatibility with existing data.
     */
    @Transactional
    public Order createOrderFromPurchaseRequest(PurchaseRequest purchaseRequest, String sellerNote) {
        if (orderRepository.findByPurchaseRequest_RequestId(purchaseRequest.getRequestId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists for this purchase request");
        }

        Order order = new Order();
        order.setPurchaseRequest(purchaseRequest);
        order.setProduct(purchaseRequest.getProduct());
        order.setBuyer(purchaseRequest.getBuyer());
        order.setSeller(purchaseRequest.getProduct().getSeller());
        order.setTotalAmount(purchaseRequest.getProduct().getPrice());
        order.setDepositAmount(purchaseRequest.getDepositAmount());
        order.setPlatformFee(purchaseRequest.getPlatformFee());
        order.setInspectionFee(purchaseRequest.getInspectionFee());
        order.setTransactionType(purchaseRequest.getTransactionType());
        order.setDesiredTransactionTime(purchaseRequest.getDesiredTransactionTime());
        order.setBuyerNote(purchaseRequest.getNote());
        order.setSellerNote(sellerNote);
        order.setStatus(OrderStatus.PENDING_DELIVERY);

        return orderRepository.save(order);
    }

    /**
     * Get order by ID - with ownership check
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        validateOrderAccess(order, userId);
        return OrderResponse.from(order);
    }

    /**
     * Get order by purchase request ID - with ownership check
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByRequestId(Integer requestId, Integer userId) {
        Order order = orderRepository.findByPurchaseRequest_RequestId(requestId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this request"));
        validateOrderAccess(order, userId);
        return OrderResponse.from(order);
    }

    private void validateOrderAccess(Order order, Integer userId) {
        boolean isBuyer = order.getBuyer().getUserId().equals(userId);
        boolean isSeller = order.getSeller().getUserId().equals(userId);
        if (!isBuyer && !isSeller) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this order");
        }
    }

    /**
     * Get orders for a buyer
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getBuyerOrders(Integer buyerId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByBuyer_UserIdAndStatus(buyerId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findByBuyer_UserId(buyerId, pageable);
            }
        } else {
            orders = orderRepository.findByBuyer_UserId(buyerId, pageable);
        }

        return orders.map(OrderResponse::from);
    }

    /**
     * Get orders for a seller
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getSellerOrders(Integer sellerId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findBySeller_UserIdAndStatus(sellerId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findBySeller_UserId(sellerId, pageable);
            }
        } else {
            orders = orderRepository.findBySeller_UserId(sellerId, pageable);
        }

        return orders.map(OrderResponse::from);
    }

    /**
     * Update order status (used by delivery flow)
     */
    @Transactional
    public void updateOrderStatus(Integer requestId, OrderStatus newStatus) {
        orderRepository.findByPurchaseRequest_RequestId(requestId).ifPresent(order -> {
            order.setStatus(newStatus);
            orderRepository.save(order);
        });
    }
}
