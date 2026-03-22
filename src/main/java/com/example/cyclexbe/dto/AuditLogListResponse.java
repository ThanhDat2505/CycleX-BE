package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.AuditLog;
import java.util.List;

public class AuditLogListResponse {

    public List<AuditLogItem> items;
    public long total;
    public int page;
    public int pageSize;
    public int totalPages;

    public AuditLogListResponse(List<AuditLogItem> items, long total, int page, int pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public static class AuditLogItem {
        public String id;
        public String actionType;
        public int adminId;
        public String adminName;
        public String targetId;
        public String details;
        public String createdAt;

        public static AuditLogItem from(AuditLog log) {
            AuditLogItem item = new AuditLogItem();
            item.id = String.valueOf(log.getId());
            item.actionType = log.getActionType().name();
            item.adminId = log.getAdminId();
            item.adminName = log.getAdminName();
            item.targetId = log.getTargetId();
            item.details = log.getDetails();
            item.createdAt = log.getCreatedAt() != null ? log.getCreatedAt().toString() : null;
            return item;
        }
    }
}
