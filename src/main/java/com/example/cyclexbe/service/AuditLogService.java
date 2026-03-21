package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.AuditLogAction;
import com.example.cyclexbe.dto.AuditLogListResponse;
import com.example.cyclexbe.entity.AuditLog;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final com.example.cyclexbe.repository.UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository,
            com.example.cyclexbe.repository.UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Log an admin action
     */
    public void log(AuditLogAction action, String targetId, String details) {
        Integer adminId = getCurrentAdminId();
        String adminName = getCurrentAdminName(adminId);

        AuditLog entry = new AuditLog();
        entry.setActionType(action);
        entry.setAdminId(adminId);
        entry.setAdminName(adminName);
        entry.setTargetId(targetId);
        entry.setDetails(details);
        auditLogRepository.save(entry);
    }

    /**
     * Get paginated audit logs with filters
     */
    public AuditLogListResponse getLogs(String actionType, Integer adminId,
            String startDate, String endDate,
            int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        AuditLogAction action = null;
        if (actionType != null && !actionType.isEmpty()) {
            action = AuditLogAction.valueOf(actionType);
        }

        LocalDateTime fromDate = startDate != null && !startDate.isEmpty()
                ? LocalDateTime.parse(startDate + "T00:00:00")
                : null;
        LocalDateTime toDate = endDate != null && !endDate.isEmpty()
                ? LocalDateTime.parse(endDate + "T23:59:59")
                : null;

        Page<AuditLog> logPage = auditLogRepository.findByFilters(action, adminId, fromDate, toDate, pageable);

        List<AuditLogListResponse.AuditLogItem> items = logPage.getContent().stream()
                .map(AuditLogListResponse.AuditLogItem::from)
                .toList();

        return new AuditLogListResponse(items, logPage.getTotalElements(), page, pageSize);
    }

    private Integer getCurrentAdminId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            return Integer.parseInt(auth.getPrincipal().toString());
        }
        return 0;
    }

    private String getCurrentAdminName(Integer adminId) {
        return userRepository.findById(adminId)
                .map(User::getFullName)
                .orElse("Unknown Admin");
    }
}
