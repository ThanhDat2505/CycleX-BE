package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.NotificationType;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.Notification;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.NotificationRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * 1) Get notification list with pagination and optional filters
     */
    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Integer userId, int page, int size,
                                                     Boolean isRead, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> notificationsPage;

        if (isRead != null && type != null) {
            NotificationType notificationType = parseType(type);
            notificationsPage = notificationRepository.findByUser_UserIdAndIsReadAndType(
                    userId, isRead, notificationType, pageable);
        } else if (isRead != null) {
            notificationsPage = notificationRepository.findByUser_UserIdAndIsRead(
                    userId, isRead, pageable);
        } else if (type != null) {
            NotificationType notificationType = parseType(type);
            notificationsPage = notificationRepository.findByUser_UserIdAndType(
                    userId, notificationType, pageable);
        } else {
            notificationsPage = notificationRepository.findByUser_UserId(userId, pageable);
        }

        List<NotificationItemDto> items = notificationsPage.getContent()
                .stream()
                .map(this::mapToDto)
                .toList();

        return new NotificationListResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                notificationsPage.getTotalElements(),
                notificationsPage.getTotalPages(),
                items
        );
    }

    /**
     * 2) Get unread count
     */
    @Transactional(readOnly = true)
    public NotificationUnreadCountResponse getUnreadCount(Integer userId) {
        long count = notificationRepository.countUnreadByUserId(userId);
        return new NotificationUnreadCountResponse(count);
    }

    /**
     * 3) Mark a single notification as read
     */
    public NotificationMarkReadResponse markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);

        return new NotificationMarkReadResponse(
                notification.getNotificationId(),
                true,
                "Notification marked as read"
        );
    }

    /**
     * 4) Mark all notifications as read
     */
    public NotificationReadAllResponse markAllAsRead(Integer userId) {
        int updatedCount = notificationRepository.markAllAsReadByUserId(userId);
        return new NotificationReadAllResponse(updatedCount, "All notifications marked as read");
    }

    /**
     * 5) Get notification detail
     */
    @Transactional(readOnly = true)
    public NotificationItemDto getNotificationDetail(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"));

        return mapToDto(notification);
    }

    /**
     * Create a notification for a specific user (used internally by other services)
     */
    public Notification createNotification(User user, String title, String message,
                                           NotificationType type, String targetType,
                                           Integer targetId, String targetUrl) {
        Notification notification = new Notification(user, title, message, type,
                targetType, targetId, targetUrl);
        return notificationRepository.save(notification);
    }

    /**
     * Create notification by userId
     */
    public Notification createNotification(Integer userId, String title, String message,
                                           NotificationType type, String targetType,
                                           Integer targetId, String targetUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
        return createNotification(user, title, message, type, targetType, targetId, targetUrl);
    }

    private NotificationItemDto mapToDto(Notification notification) {
        return new NotificationItemDto(
                notification.getNotificationId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getTargetType(),
                notification.getTargetId(),
                notification.getTargetUrl()
        );
    }

    private NotificationType parseType(String type) {
        try {
            return NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification type: " + type);
        }
    }
}
