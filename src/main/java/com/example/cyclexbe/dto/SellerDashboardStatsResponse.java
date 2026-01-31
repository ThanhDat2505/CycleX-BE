package com.example.cyclexbe.dto;

public class SellerDashboardStatsResponse {
    public long approvedCount;
    public long pendingCount;
    public long rejectedCount;
    public long totalListings;
    public long totalViews;

    public SellerDashboardStatsResponse() {}

    public SellerDashboardStatsResponse(long approvedCount, long pendingCount, long rejectedCount, long totalListings, long totalViews) {
        this.approvedCount = approvedCount;
        this.pendingCount = pendingCount;
        this.rejectedCount = rejectedCount;
        this.totalListings = totalListings;
        this.totalViews = totalViews;
    }
}
