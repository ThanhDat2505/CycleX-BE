package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardResponse {

    public UserManagementStats userManagement;
    public DisputeManagementStats disputeManagement;
    public TransactionStats transactions;
    public WeeklyStats weeklyStats;

    public static class UserManagementStats {
        public long totalUsers;
        public long activeUsers;
        public long bannedSuspendedUsers;
        public long newUsersInRange;
    }

    public static class DisputeManagementStats {
        public long totalDisputes;
        public long pendingDisputes;
        public long resolvedDisputes;
        public long newDisputesInRange;
    }

    public static class TransactionStats {
        public long totalSuccessfulTransactions;
        public BigDecimal successfulRevenue;
    }

    public static class WeeklyStats {
        public List<WeeklyDataPoint> listings;
        public List<WeeklyDataPoint> products;
        public List<WeeklyDataPoint> orders;
    }

    public static class WeeklyDataPoint {
        public String week;
        public long count;

        public WeeklyDataPoint() {}

        public WeeklyDataPoint(String week, long count) {
            this.week = week;
            this.count = count;
        }
    }
}
