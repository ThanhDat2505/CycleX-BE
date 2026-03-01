package com.example.cyclexbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Response DTO for shipper dashboard summary (S-60 F1)
 * GET /api/shipper/dashboard/summary
 * Returns counts of assigned, in-progress, and failed deliveries for current shipper
 */
public class ShipperDashboardSummaryResponse {

    private ShipperDashboardCountsDto counts;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime asOf;

    public ShipperDashboardSummaryResponse() {}

    public ShipperDashboardSummaryResponse(ShipperDashboardCountsDto counts, LocalDateTime asOf) {
        this.counts = counts;
        this.asOf = asOf;
    }

    public ShipperDashboardCountsDto getCounts() { return counts; }
    public void setCounts(ShipperDashboardCountsDto counts) { this.counts = counts; }

    public LocalDateTime getAsOf() { return asOf; }
    public void setAsOf(LocalDateTime asOf) { this.asOf = asOf; }
}

