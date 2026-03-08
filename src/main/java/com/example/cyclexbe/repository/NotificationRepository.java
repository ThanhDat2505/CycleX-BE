package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.NotificationType;
import com.example.cyclexbe.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByUser_UserId(Integer userId, Pageable pageable);

    Page<Notification> findByUser_UserIdAndIsRead(Integer userId, boolean isRead, Pageable pageable);

    Page<Notification> findByUser_UserIdAndType(Integer userId, NotificationType type, Pageable pageable);

    Page<Notification> findByUser_UserIdAndIsReadAndType(Integer userId, boolean isRead, NotificationType type, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") Integer userId);

    Optional<Notification> findByNotificationIdAndUser_UserId(Integer notificationId, Integer userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);
}
