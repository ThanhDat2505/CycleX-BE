package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 1) GET /api/notifications?page=0&size=10&isRead=false&type=DELIVERY_SUCCESS
     * Get notification list with pagination and optional filters
     */
    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String type) {

        Integer userId = extractUserIdFromAuth();
        NotificationListResponse response = notificationService.getNotifications(userId, page, size, isRead, type);
        return ResponseEntity.ok(response);
    }

    /**
     * 2) GET /api/notifications/unread-count
     * Get unread notification count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<NotificationUnreadCountResponse> getUnreadCount() {
        Integer userId = extractUserIdFromAuth();
        NotificationUnreadCountResponse response = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 3) PATCH /api/notifications/{notificationId}/read
     * Mark a single notification as read
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationMarkReadResponse> markAsRead(
            @PathVariable Integer notificationId) {

        Integer userId = extractUserIdFromAuth();
        NotificationMarkReadResponse response = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 4) PATCH /api/notifications/read-all
     * Mark all notifications as read
     */
    @PatchMapping("/read-all")
    public ResponseEntity<NotificationReadAllResponse> markAllAsRead() {
        Integer userId = extractUserIdFromAuth();
        NotificationReadAllResponse response = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 5) GET /api/notifications/{notificationId}
     * Get notification detail
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationItemDto> getNotificationDetail(
            @PathVariable Integer notificationId) {

        Integer userId = extractUserIdFromAuth();
        NotificationItemDto response = notificationService.getNotificationDetail(notificationId, userId);
        return ResponseEntity.ok(response);
    }

    private Integer extractUserIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || "anonymousUser".equals(principal)) {
            throw new RuntimeException("User is not authenticated");
        }

        if (principal instanceof String principalStr) {
            try {
                return Integer.parseInt(principalStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID in authentication: " + principalStr);
            }
        }

        throw new RuntimeException("Unsupported authentication principal type: " + principal.getClass().getName());
    }
}
