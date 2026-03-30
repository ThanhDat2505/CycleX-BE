package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.*;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BikeListingRepository bikeListingRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DisputeRepository disputeRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
            BikeListingRepository bikeListingRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            DisputeRepository disputeRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bikeListingRepository = bikeListingRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.disputeRepository = disputeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== USER MANAGEMENT ====================

    public AdminUserListResponse getUsers(String search, String role, String status,
            int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage = userRepository.findByAdminFilters(
                search,
                role != null ? Role.valueOf(role) : null,
                status,
                pageable);

        List<UserResponse> items = userPage.getContent().stream()
                .map(UserResponse::from)
                .toList();

        return new AdminUserListResponse(items, userPage.getTotalElements(), page, pageSize);
    }

    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        return UserResponse.from(user);
    }

    public UserResponse updateUserStatus(Integer userId, String newStatus) {
        if (!"ACTIVE".equals(newStatus) && !"SUSPENDED".equals(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Trạng thái không hợp lệ. Phải là ACTIVE hoặc SUSPENDED");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể thay đổi trạng thái của admin");
        }

        user.setStatus(newStatus);
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse updateUserRole(Integer userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể thay đổi vai trò của admin");
        }

        user.setRole(newRole);
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse createAccount(AdminCreateAccountRequest req) {
        if (req.role != Role.SHIPPER && req.role != Role.INSPECTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Admin chỉ có thể tạo tài khoản SHIPPER hoặc INSPECTOR");
        }

        if (userRepository.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        User user = new User();
        user.setEmail(req.email);
        user.setPasswordHash(passwordEncoder.encode(req.password));
        user.setFullName(req.fullName);
        user.setPhone(req.phone);
        user.setRole(req.role);
        user.setCccd(req.cccd);
        user.setAddress(req.address);
        user.setStatus("ACTIVE");
        user.setVerify(true); // Admin-created accounts are always verified

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    // ==================== DASHBOARD STATISTICS ====================

    public AdminDashboardResponse getDashboardData(String timeRange, String startDate, String endDate) {
        LocalDateTime from = calculateFromDate(timeRange, startDate);
        LocalDateTime to = calculateToDate(timeRange, endDate);

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.userManagement = getUserManagementStats(from, to);
        response.disputeManagement = getDisputeManagementStats(from, to);
        response.transactions = getTransactionStats();
        response.weeklyStats = getWeeklyStats();
        return response;
    }

    private AdminDashboardResponse.UserManagementStats getUserManagementStats(LocalDateTime from, LocalDateTime to) {
        var stats = new AdminDashboardResponse.UserManagementStats();
        stats.totalUsers = userRepository.count();
        stats.activeUsers = userRepository.countByStatus("ACTIVE");
        stats.bannedSuspendedUsers = userRepository.countByStatus("SUSPENDED");
        stats.newUsersInRange = userRepository.countByCreatedAtBetween(from, to);
        return stats;
    }

    private AdminDashboardResponse.DisputeManagementStats getDisputeManagementStats(LocalDateTime from,
            LocalDateTime to) {
        var stats = new AdminDashboardResponse.DisputeManagementStats();
        stats.totalDisputes = disputeRepository.count();
        stats.pendingDisputes = disputeRepository.countByStatus(DisputeStatus.OPEN)
                + disputeRepository.countByStatus(DisputeStatus.IN_PROGRESS)
                + disputeRepository.countByStatus(DisputeStatus.ESCALATED);
        stats.resolvedDisputes = disputeRepository.countByStatus(DisputeStatus.RESOLVED)
                + disputeRepository.countByStatus(DisputeStatus.REJECTED);
        stats.newDisputesInRange = disputeRepository.countByCreatedAtBetween(from, to);
        return stats;
    }

    private AdminDashboardResponse.TransactionStats getTransactionStats() {
        var stats = new AdminDashboardResponse.TransactionStats();
        stats.totalSuccessfulTransactions = orderRepository.countByStatus(OrderStatus.COMPLETED)
                + orderRepository.countByStatus(OrderStatus.DELIVERED);
        BigDecimal revenue = orderRepository.sumTotalAmountByStatuses(
                List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED));
        stats.successfulRevenue = revenue != null ? revenue : BigDecimal.ZERO;
        return stats;
    }

    private AdminDashboardResponse.WeeklyStats getWeeklyStats() {
        var stats = new AdminDashboardResponse.WeeklyStats();

        // Get data for last 8 weeks
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eightWeeksAgo = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .minusWeeks(7).withHour(0).withMinute(0).withSecond(0).withNano(0);

        stats.listings = new ArrayList<>();
        stats.products = new ArrayList<>();
        stats.orders = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            LocalDateTime weekStart = eightWeeksAgo.plusWeeks(i);
            LocalDateTime weekEnd = weekStart.plusWeeks(1);
            String weekLabel = String.format("T%d", i + 1);

            long listingCount = bikeListingRepository.countByCreatedAtBetween(weekStart, weekEnd);
            long productCount = productRepository.countByCreatedAtBetween(weekStart, weekEnd);
            long orderCount = orderRepository.countByCreatedAtBetween(weekStart, weekEnd);

            stats.listings.add(new AdminDashboardResponse.WeeklyDataPoint(weekLabel, listingCount));
            stats.products.add(new AdminDashboardResponse.WeeklyDataPoint(weekLabel, productCount));
            stats.orders.add(new AdminDashboardResponse.WeeklyDataPoint(weekLabel, orderCount));
        }

        return stats;
    }

    private LocalDateTime calculateFromDate(String timeRange, String startDate) {
        LocalDateTime now = LocalDateTime.now();
        return switch (timeRange) {
            case "TODAY" -> now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "LAST_7_DAYS" -> now.minusDays(7);
            case "LAST_30_DAYS" -> now.minusDays(30);
            case "CUSTOM" -> startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : now.minusDays(7);
            default -> now.minusDays(7);
        };
    }

    private LocalDateTime calculateToDate(String timeRange, String endDate) {
        LocalDateTime now = LocalDateTime.now();
        if ("CUSTOM".equals(timeRange) && endDate != null) {
            return LocalDateTime.parse(endDate + "T23:59:59");
        }
        return now;
    }
}
