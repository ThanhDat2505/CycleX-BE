package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.AdminService;
import com.example.cyclexbe.service.AuditLogService;
import com.example.cyclexbe.domain.enums.AuditLogAction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;

    public AdminController(AdminService adminService, AuditLogService auditLogService) {
        this.adminService = adminService;
        this.auditLogService = auditLogService;
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(
            @RequestParam(defaultValue = "LAST_7_DAYS") String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(adminService.getDashboardData(timeRange, startDate, endDate));
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public ResponseEntity<AdminUserListResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(adminService.getUsers(search, role, status, page, pageSize));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Integer userId,
            @Valid @RequestBody AdminUpdateStatusRequest req) {
        UserResponse result = adminService.updateUserStatus(userId, req.status);
        auditLogService.log(AuditLogAction.UPDATE_STATUS, String.valueOf(userId),
                "Changed status to " + req.status + " for user: " + result.fullName);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Integer userId,
            @Valid @RequestBody AdminUpdateRoleRequest req) {
        UserResponse result = adminService.updateUserRole(userId, req.role);
        auditLogService.log(AuditLogAction.UPDATE_ROLE, String.valueOf(userId),
                "Changed role to " + req.role + " for user: " + result.fullName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createAccount(@Valid @RequestBody AdminCreateAccountRequest req) {
        return adminService.createAccount(req);
    }

    // ==================== AUDIT LOGS ====================

    @GetMapping("/audit-logs")
    public ResponseEntity<AuditLogListResponse> getAuditLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) Integer adminId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        return ResponseEntity.ok(auditLogService.getLogs(actionType, adminId, startDate, endDate, page, pageSize));
    }
}
