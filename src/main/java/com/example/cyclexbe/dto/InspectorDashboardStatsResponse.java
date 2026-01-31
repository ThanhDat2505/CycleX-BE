package com.example.cyclexbe.dto;

public class InspectorDashboardStatsResponse {
    public long pendingCount;
    public long reviewingCount;
    public long approvedCount;
    public long rejectedCount;
    public long disputeCount;

    public InspectorDashboardStatsResponse() {}

    public InspectorDashboardStatsResponse(long pendingCount, long reviewingCount,
                                          long approvedCount, long rejectedCount, long disputeCount) {
        this.pendingCount = pendingCount;
        this.reviewingCount = reviewingCount;
        this.approvedCount = approvedCount;
        this.rejectedCount = rejectedCount;
        this.disputeCount = disputeCount;
    }
}
